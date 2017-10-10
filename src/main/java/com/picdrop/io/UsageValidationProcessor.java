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
import static com.picdrop.helper.LogHelper.PROCESSOR_USAGE_VALIDATION;
import com.picdrop.model.RequestContext;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public class UsageValidationProcessor extends AbstractProcessor<FileResource> {

    Logger log = LogManager.getLogger();

    @Inject
    Provider<RequestContext> contextProv;

    Repository<String, RegisteredUser> userRepo;

    @Inject
    public UsageValidationProcessor(Repository<String, RegisteredUser> userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public FileResource onPreStore(FileResource entity, ResourceContainer cnt) throws IOException, ApplicationException {
        RegisteredUser u = contextProv.get().getPrincipal();

        if (u == null) {
            log.warn(PROCESSOR_USAGE_VALIDATION, "Unable to resolve a principal where there should be one");
            return super.onPreStore(entity, cnt);
        }

        if (u.getSizeLimit() < (u.getSizeUsage() + cnt.getFileSize())) {
            throw new ApplicationException()
                    .status(400)
                    .devMessage(String.format("Current usage: %d/%d", u.getSizeUsage(), u.getSizeLimit()))
                    .code(ErrorMessageCode.LIMIT_STORAGE_FULL);
        }

        return super.onPreStore(entity, cnt); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPostDelete(FileResource entity) throws IOException, ApplicationException {
        RegisteredUser u = contextProv.get().getPrincipal();

        if (u == null) {
            log.warn(PROCESSOR_USAGE_VALIDATION, "Unable to resolve a principal where there should be one");
            super.onPostDelete(entity);
            return;
        }

        u.incSizeUsage(entity.getSize() * -1);
        userRepo.update(u.getId(), u);

        super.onPostDelete(entity);
    }

    @Override
    public FileResource onPostStore(FileResource entity, ResourceContainer cnt) throws IOException, ApplicationException {
        RegisteredUser u = contextProv.get().getPrincipal();

        if (u == null) {
            log.warn(PROCESSOR_USAGE_VALIDATION, "Unable to resolve a principal where there should be one");
            return super.onPostStore(entity, cnt);
        }

        u.incSizeUsage(cnt.getFileSize());
        userRepo.update(u.getId(), u);

        return super.onPostStore(entity, cnt);
    }

}
