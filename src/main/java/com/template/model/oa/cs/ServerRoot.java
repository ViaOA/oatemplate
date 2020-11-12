// Copied from OATemplate project by OABuilder 07/01/16 07:41 AM
package com.template.model.oa.cs;

import java.util.*;
import javax.xml.bind.annotation.*;

import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;
import com.viaoa.annotation.*;
import com.viaoa.hub.*;
import com.viaoa.object.*;
import com.viaoa.util.*;

/**
 * Root Object that is automatically updated between the Server and Clients. ServerController will do the selects for these objects. Model
 * will share these hubs after the application is started.
 */

@OAClass(useDataSource = false, displayProperty = "Id")
@XmlRootElement(name = "serverRoot")
@XmlType(factoryMethod = "jaxbCreate")
@XmlAccessorType(XmlAccessType.NONE)
public class ServerRoot extends OAObject {
	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_Id = "Id";
	public static final String P_Id = "Id";

	/*$$Start: ServerRoot1 $$*/
	// lookups, preselects, autoCreated
	public static final String P_CreateOneAppServerHub = "CreateOneAppServerHub";
	public static final String P_AppUsers = "AppUsers";
	// filters
	// UI containers
	public static final String P_AppUserLogins = "AppUserLogins";
	public static final String P_AppUserErrors = "AppUserErrors";
	/*$$End: ServerRoot1 $$*/

	protected int id;
	/*$$Start: ServerRoot2 $$*/
	// lookups, preselects, autoCreated
	protected transient Hub<AppServer> hubCreateOneAppServer;
	protected transient Hub<AppUser> hubAppUsers;
	// filters
	// UI containers
	protected transient Hub<AppUserLogin> hubAppUserLogins;
	protected transient Hub<AppUserError> hubAppUserErrors;
	/*$$End: ServerRoot2 $$*/

	public ServerRoot() {
		setId(777);
	}

	@XmlAttribute(name = "oaSingleId")
	public Integer getJaxbGuid() {
		return super.getJaxbGuid();
	}

	@OAProperty(displayName = "Id")
	@OAId
	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int id) {
		int old = this.id;
		this.id = id;
		firePropertyChange(PROPERTY_Id, old, id);
	}

	@XmlID
	@XmlAttribute(name = "id")
	public String getJaxbId() {
		// note: jaxb spec requires id to be a string
		if (!getJaxbShouldInclude(P_Id)) {
			return null;
		}
		return "" + id;
	}

	public void setJaxbId(String id) {
		if (getJaxbAllowPropertyChange(P_Id, this.id, id)) {
			setId((int) OAConv.convert(int.class, id));
		}
	}

	/*$$Start: ServerRoot3 $$*/
	// lookups, preselects, autoCreated
	@OAMany(toClass = AppServer.class, cascadeSave = true)
	public Hub<AppServer> getCreateOneAppServerHub() {
		if (hubCreateOneAppServer == null) {
			hubCreateOneAppServer = (Hub<AppServer>) super.getHub(P_CreateOneAppServerHub);
		}
		return hubCreateOneAppServer;
	}

	@OAMany(toClass = AppUser.class, cascadeSave = true)
	public Hub<AppUser> getAppUsers() {
		if (hubAppUsers == null) {
			hubAppUsers = (Hub<AppUser>) super.getHub(P_AppUsers);
		}
		return hubAppUsers;
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

	public static ServerRoot jaxbCreate() {
		ServerRoot serverRoot = (ServerRoot) OAObject.jaxbCreateInstance(ServerRoot.class);
		if (serverRoot == null) {
			serverRoot = new ServerRoot();
		}
		return serverRoot;
	}
}
