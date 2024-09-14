// Copied from OATemplate project by OABuilder 07/01/16 07:41 AM
package com.template.model.oa.cs;

import java.util.*;

import com.viaoa.annotation.*;
import com.viaoa.hub.*;
import com.viaoa.object.*;
import com.viaoa.util.*;
import com.template.model.oa.ReportClass;
import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;

/**
 * Root Object that is automatically updated between the Server and Clients. ServerController will do the selects for these objects. Model
 * will share these hubs after the application is started.
 */

@OAClass(useDataSource = false, displayProperty = "Id")
public class ServerRoot extends OAObject {
	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_Id = "Id";
	public static final String P_Id = "Id";

	/*$$Start: ServerRoot1 $$*/
    // lookups, preselects
    public static final String P_AppServers = "AppServers";
    public static final String P_AppUsers = "AppUsers";
    public static final String P_ReportClasses = "ReportClasses";
    // autoCreateOne
    public static final String P_CreateOneAppServerHub = "CreateOneAppServerHub";
    // filters
    // UI containers
    public static final String P_AppUserLogins = "AppUserLogins";
    public static final String P_AppUserErrors = "AppUserErrors";
/*$$End: ServerRoot1 $$*/

	protected int id;
	/*$$Start: ServerRoot2 $$*/
    // lookups, preselects
    protected transient Hub<AppServer> hubAppServers;
    protected transient Hub<AppUser> hubAppUsers;
    protected transient Hub<ReportClass> hubReportClasses;
    // autoCreateOne
    protected transient Hub<AppServer> hubCreateOneAppServer;
    // filters
    // UI containers
    protected transient Hub<AppUserLogin> hubAppUserLogins;
    protected transient Hub<AppUserError> hubAppUserErrors;
/*$$End: ServerRoot2 $$*/

	public ServerRoot() {
		setId(777);
	}

	@OAProperty(displayName = "Id")
	@OAId
	public int getId() {
		return id;
	}

	public void setId(int id) {
		int old = this.id;
		this.id = id;
		firePropertyChange(PROPERTY_Id, old, id);
	}

	/*$$Start: ServerRoot3 $$*/
    // lookups, preselects
    @OAMany(toClass = AppServer.class, cascadeSave = true)
    public Hub<AppServer> getAppServers() {
        if (hubAppServers == null) {
            hubAppServers = (Hub<AppServer>) super.getHub(P_AppServers);
        }
        return hubAppServers;
    }
    @OAMany(toClass = AppUser.class, cascadeSave = true)
    public Hub<AppUser> getAppUsers() {
        if (hubAppUsers == null) {
            hubAppUsers = (Hub<AppUser>) super.getHub(P_AppUsers);
        }
        return hubAppUsers;
    }
    @OAMany(toClass = ReportClass.class, cascadeSave = true)
    public Hub<ReportClass> getReportClasses() {
        if (hubReportClasses == null) {
            hubReportClasses = (Hub<ReportClass>) super.getHub(P_ReportClasses);
        }
        return hubReportClasses;
    }
    // autoCreatedOne
    @OAMany(toClass = AppServer.class, cascadeSave = true)
    public Hub<AppServer> getCreateOneAppServerHub() {
        if (hubCreateOneAppServer == null) {
            hubCreateOneAppServer = (Hub<AppServer>) super.getHub(P_CreateOneAppServerHub);
        }
        return hubCreateOneAppServer;
    }
    // filters
    // UI containers
    @OAMany(toClass = AppUserLogin.class, isCalculated = true, cascadeSave = true)
    public Hub<AppUserLogin> getAppUserLogins() {
        if (hubAppUserLogins == null) {
            hubAppUserLogins = (Hub<AppUserLogin>) super.getHub(P_AppUserLogins);
            String pp = AppUserPP.appUserLogins().lastDayFilter().pp;
            HubMerger hm = new HubMerger(this.getAppUsers(), hubAppUserLogins, pp, false, true);
        }
        return hubAppUserLogins;
    }
    @OAMany(toClass = AppUserError.class, isCalculated = true, cascadeSave = true)
    public Hub<AppUserError> getAppUserErrors() {
        if (hubAppUserErrors == null) {
            hubAppUserErrors = (Hub<AppUserError>) super.getHub(P_AppUserErrors);
            String pp = AppUserPP.appUserLogins().appUserErrors().pp;
            HubMerger hm = new HubMerger(this.getAppUsers(), hubAppUserErrors, pp, false, true);
        }
        return hubAppUserErrors;
    }
/*$$End: ServerRoot3 $$*/
}
