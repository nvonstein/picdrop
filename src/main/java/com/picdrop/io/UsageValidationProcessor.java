/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.guice.provider.ResourceContainer;
import com.picdrop.model.RequestContext;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public class UsageValidationProcessor extends AbstractProcessor<FileResource> {

    @Inject
    Provider<RequestContext> contextProv;

    Repository<String, RegisteredUser> userRepo;

    @Inject
    public UsageValidationProcessor(Repository<String, RegisteredUser> userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public FileResource onPreStore(FileResource entity, ResourceContainer cnt) throws IOException, ApplicationException {
        User u = contextProv.get().getPrincipal();

        if (u.isRegistered()) {
            RegisteredUser regUser = u.to(RegisteredUser.class);
            if (regUser.getSizeLimit() < (regUser.getSizeUsage() + cnt.getFileSize())) {
                throw new ApplicationException()
                        .status(400)
                        .devMessage(String.format("Current usage: %d/%d", regUser.getSizeUsage(), regUser.getSizeLimit()))
                        .code(ErrorMessageCode.LIMIT_STORAGE_FULL);
            }
        }
        return super.onPreStore(entity, cnt); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPostDelete(FileResource entity) throws IOException, ApplicationException {
        User u = contextProv.get().getPrincipal();

        if (u.isRegistered()) {
            RegisteredUser regUser = u.to(RegisteredUser.class);
            regUser.incSizeUsage(entity.getSize() * -1);
            userRepo.update(regUser.getId(), regUser);
        }

        super.onPostDelete(entity);
    }

    @Override
    public FileResource onPostStore(FileResource entity, ResourceContainer cnt) throws IOException, ApplicationException {
        User u = contextProv.get().getPrincipal();

        if (u.isRegistered()) {
            RegisteredUser regUser = u.to(RegisteredUser.class);
            regUser.incSizeUsage(cnt.getFileSize());
            userRepo.update(regUser.getId(), regUser);
        }
        return super.onPostStore(entity, cnt);
    }

}
