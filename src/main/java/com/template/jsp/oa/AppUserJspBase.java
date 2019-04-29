package com.template.jsp.oa;

import com.template.model.*;
import com.template.model.oa.*;
import com.viaoa.hub.*;
import com.viaoa.jsp.*;

public class AppUserJspBase {

    public static final String JSP_lblId = "lblId";
    public static final String JSP_txtFirstName = "txtFirstName";
    public static final String JSP_txtLastName = "txtLastName";
    public static final String JSP_ptxtPassword = "ptxtPassword";
    public static final String JSP_chkAdmin = "chkAdmin";
    public static final String JSP_txtInactiveDate = "txtInactiveDate";
    public static final String JSP_txtCreated = "txtCreated";
    public static final String JSP_cbo = "cbo";
    public static final String JSP_select = "select";
    public static final String JSP_table = "table";
    public static final String JSP_list = "list";
    public static final String JSP_grid = "grid";

    
    protected AppUserModel model;
    protected OAForm form;
    protected String idPrefix;

    protected AppUserLoginJsp jspAppUserLogins;
    
    protected OALabel lblId;
    protected OATextField txtFirstName;
    protected OATextField txtLastName;
    protected OACheckBox chkAdmin;
    protected OAPassword ptxtPassword;
    protected OATextField txtInactiveDate;
    protected OACombo cbo;
    protected OAHtmlSelect select;
    protected OATable table;
    protected OAList list;
    protected OAGrid grid;
    
    
    public AppUserJspBase(Hub<AppUser> hub, OAForm form) {
        this(new AppUserModel(hub), form, "");
    }    
    public AppUserJspBase(AppUserModel model, OAForm form) {
        this(model, form, "");
    }    
    public AppUserJspBase(Hub<AppUser> hub, OAForm form, String idPrefix) {
        this(new AppUserModel(hub), form, idPrefix);
    }    
    public AppUserJspBase(AppUserModel model, OAForm form, String idPrefix) {
        this.model = model;
        this.form = form;
        this.idPrefix = (idPrefix == null ? "" : idPrefix);
    }    

    public AppUserModel getModel() {
        return model;
    }
    public OAForm getForm() {
        return form;
    }
    
    public AppUserLoginJsp getAppUserLoginsJsp() {
        if (jspAppUserLogins != null) return jspAppUserLogins;
        jspAppUserLogins = new AppUserLoginJsp(getModel().getAppUserLoginsModel(), getForm(), AppUser.P_AppUserLogins);
        return jspAppUserLogins;
    }
    
    public OALabel getIdLabel() {
        if (lblId == null) {
            lblId = createIdLabel(idPrefix+JSP_lblId);
        }
        return lblId;
    }
    public OALabel createIdLabel(String id) {
        OALabel lbl = new OALabel(id, model.getHub(), AppUser.P_Id);
        return lbl;
    }

    public OATextField getFirstNameTextField() {
        if (txtFirstName == null) {
            txtFirstName = createFirstNameTextField(idPrefix+JSP_txtFirstName);
        }
        return txtFirstName;
    }
    public OATextField createFirstNameTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUser.P_FirstName, 10, 25);
        return txt;
    }

    public OATextField getLastNameTextField() {
        if (txtLastName == null) {
            txtLastName = createLastNameTextField(idPrefix+JSP_txtLastName);
        }
        return txtLastName;
    }
    public OATextField createLastNameTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUser.P_LastName, 10, 35);
        txt.setRequired(true);
        return txt;
    }

    public OAPassword getPasswordPassword() {
        if (ptxtPassword == null) {
            ptxtPassword = createPasswordPassword(idPrefix+JSP_ptxtPassword);
        }
        return ptxtPassword;
    }
    public OAPassword createPasswordPassword(String id) {
        OAPassword ptxt = new OAPassword(id, model.getHub(), AppUser.P_Password, 10, 35);
        // ptxtPassword.setRequired(true);
        return ptxt;
    }

    public OACheckBox getAdminCheckBox() {
        if (chkAdmin == null) {
            chkAdmin = createtAdminCheckBox(idPrefix+JSP_chkAdmin);
        }
        return chkAdmin;
    }
    public OACheckBox createtAdminCheckBox(String id) {
        OACheckBox chk = new OACheckBox(id, model.getHub(), AppUser.P_Admin);
        return chk;
    }
    
    public OATextField getInactiveDateTextField() {
        if (txtInactiveDate == null) {
            txtInactiveDate = createInactiveDateTextField(idPrefix+JSP_txtInactiveDate);
        }
        return txtInactiveDate;
    }
    public OATextField createInactiveDateTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUser.P_InactiveDate, 10, 35);
        return txt;
    }

    public OACombo getCombo() {
        if (cbo == null) {
            cbo = createCombo(idPrefix+JSP_cbo);
            cbo.setAllowSearch(true);
        }
        return cbo;
    }
    public OACombo createCombo(String id) {
        OACombo cbo = new OACombo(id, model.getHub(), AppUser.P_FullName, 25);
        // cbo.setRecursive(false); 
        cbo.setNullDescription("Select an AppUser");
        cbo.setAjaxSubmit(true);
        cbo.setHtmlTemplate("<%="+AppUser.P_FullName+"%><br>aa'bb\"xxxx");
        return cbo;
    }

    public OAHtmlSelect getSelect() {
        if (select == null) {
            select = createSelect(idPrefix+JSP_select);
        }
        return select;
    }
    public OAHtmlSelect createSelect(String id) {
        OAHtmlSelect sel = new OAHtmlSelect(id, model.getHub(), AppUser.P_FullName, 10, 25);
        
        // hsel.setRecursive(true);
        sel.setAjaxSubmit(true);
        return sel;
    }

    public OATable getTable() {
        if (table == null) {
            table = createTable(idPrefix+JSP_table);
        }
        return table;
    }
    public OATable createTable(String id) {
        OATable table = new OATable(id, model.getHub()) {
            public String onSubmit(String forwardUrl) {
                AppUser au = (AppUser) getHub().getAO();
                if (au != null) {
                    getForm().addScript("$('#status').html('you selected " + au.getFullName() + "');");
                }
                //((OADialog)getForm().getComponent("dlgTest")).setVisible(true);
                return super.onSubmit(forwardUrl);
            }
        };

        if (model.getAllowMultiSelect()) {
            table.setSelectHub(model.getMultiSelectHub());
        }
        table.setAjaxSubmit(true);
        table.setPager(10, 0, 5, true, true);
        addColumns(table);
        return table;
    }

    public void addColumns(OATable table) {
        OATableColumn tc = new OATableColumn(AppUser.P_FirstName, "First Name", 12);
        
        OATextField txt = createFirstNameTextField("txtTableFirstName");
        tc.setEditor(txt);
        table.addColumn(tc);

        tc = new OATableColumn(AppUser.P_LastName, "Last Name", 15);
        txt = createLastNameTextField("txtTableLastName");
        tc.setEditor(txt);
        table.addColumn(tc);

        tc = new OATableColumn(AppUser.P_FullName, "Full Name", 22);
        table.addColumn(tc);

        table.addColumn(AppUser.P_InactiveDate, "Inactive", 12);

        // or a tc can be created
        tc = new OATableColumn(AppUser.P_FullName, "Full Name", 22) {
            protected String getHtml(Hub hub, Object object, int row, boolean bHeading) {
                if (bHeading) return "Employee Full Name";
                return super.getHtml(hub, object, row, bHeading);
            }
        };
        table.addColumn(tc);
        // create column with 2 pieces of data
        //tc = new OATableColumn(new String[]{AppUser.P_FirstName, AppUser.P_LastName}, "Fname/Lname", 12);
        //table.addColumn(tc);
    }
    
    
    public OAList getList() {
        if (list == null) {
            list = createList(JSP_list);
        }
        return list;
    }
    public OAList createList(String id) {
        OAList list = new OAList(id, model.getHub(), AppUser.P_FullName, 20, 20) {
            public String getHtml(Object obj, int pos) {
                if (obj == null) return "set AO to null";
                String s = super.getHtml(obj, pos);
                //s += "." + pos;
                return s;
            }
        };
        list.setNullDescription("set AO to null");
        list.setRequired(true);
        return list;
    }
    
    public OAGrid getGrid() {
        if (grid == null) {
            grid = createGrid(JSP_grid);
        }
        return grid;
    }
    public OAGrid createGrid(String id) {
        OAGrid grid = new OAGrid(id, model.getHub(), 4);
        grid.setHtmlTemplate("<div style='border: 5px green solid; padding: 12px; height: 100px; width: 200px; overflow: hidden;'><nobr>Fullname is <%=fullName%></nobr></div>");
        grid.setAjaxSubmit(true);
        grid.setPager(5, 0, 7, true, true);
        return grid;
    }
    
    
    
}



