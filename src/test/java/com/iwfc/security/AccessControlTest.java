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

/**
 * [Unit Testing] Tests for role-based access control (RBAC) in the Facade.
 * Note: these tests use SystemManager.getInstance(), a Singleton, so the state is shared with other tests in the same JVM.
 * The test users use unique usernames to avoid clashing with shared data.
 */
class AccessControlTest {

    // Verifies: a Member calling an admin-only method gets UnauthorizedAccessException.
    // assertThrows takes a lambda, so the call runs inside the assertion.
    @Test
    void memberAccessingGlobalLog_throwsUnauthorizedAccessException() {
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());
        User member = new Member("access-test-member", "Access Test Member");

        assertThrows(UnauthorizedAccessException.class, () -> facade.viewGlobalMaintenanceLog(member));
    }

    // Verifies: an Administrator is allowed; assertDoesNotThrow confirms no exception is raised.
    @Test
    void administratorAccessingGlobalLog_succeeds() {
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());
        User admin = new Administrator("access-test-admin", "Access Test Admin");

        assertDoesNotThrow(() -> facade.viewGlobalMaintenanceLog(admin));
    }
}
