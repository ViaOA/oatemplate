// Copied from OATemplate project by OABuilder 07/01/16 07:41 AM
package com.template.model.oa.propertypath;

/**
 * Used to build compiler safe property paths.
 * @author vvia
 */
public class PP {
    /*$$Start: PPInterface.code $$*/
    public static AppServerPPx appServer() {
        return new AppServerPPx("AppServer");
    }
    public static AppServerPPx appServers() {
        return new AppServerPPx("AppServers");
    }
    public static AppUserPPx appUser() {
        return new AppUserPPx("AppUser");
    }
    public static AppUserPPx appUsers() {
        return new AppUserPPx("AppUsers");
    }
    public static AppUserErrorPPx appUserError() {
        return new AppUserErrorPPx("AppUserError");
    }
    public static AppUserErrorPPx appUserErrors() {
        return new AppUserErrorPPx("AppUserErrors");
    }
    public static AppUserLoginPPx appUserLogin() {
        return new AppUserLoginPPx("AppUserLogin");
    }
    public static AppUserLoginPPx appUserLogins() {
        return new AppUserLoginPPx("AppUserLogins");
    }
    public static ImageStorePPx imageStore() {
        return new ImageStorePPx("ImageStore");
    }
    public static ImageStorePPx imageStores() {
        return new ImageStorePPx("ImageStores");
    }
    /*$$End: PPInterface.code $$*/
}
