package com.iwfc.security;

import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.Administrator;
import com.iwfc.model.Member;
import com.iwfc.model.User;
import com.iwfc.pattern.creational.SystemManager;
import com.iwfc.pattern.structural.IWFCFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccessControlTest {

    @Test
    void memberAccessingGlobalLog_throwsUnauthorizedAccessException() {
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());
        User member = new Member("access-test-member", "Access Test Member");

        assertThrows(UnauthorizedAccessException.class, () -> facade.viewGlobalMaintenanceLog(member));
    }

    @Test
    void administratorAccessingGlobalLog_succeeds() {
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());
        User admin = new Administrator("access-test-admin", "Access Test Admin");

        assertDoesNotThrow(() -> facade.viewGlobalMaintenanceLog(admin));
    }
}
