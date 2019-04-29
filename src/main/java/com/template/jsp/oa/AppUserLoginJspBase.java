package com.template.jsp.oa;

import com.template.model.*;
import com.template.model.oa.*;
import com.viaoa.hub.*;
import com.viaoa.jsp.*;

public class AppUserLoginJspBase {

    public static final String JSP_lblId = "lblId";
    public static final String JSP_txtCreated = "txtCreated";
    public static final String JSP_txtDisconnected = "txtDisconnected";
    public static final String JSP_txtComputerName = "txtComputerName";
    public static final String JSP_cbo = "cbo";
    public static final String JSP_select = "select";
    public static final String JSP_table = "table";
    public static final String JSP_list = "list";
    
    protected AppUserLoginModel model;
    protected OAForm form;
    protected String idPrefix;

    protected OALabel lblId;
    protected OATextField txtCreated;
    protected OATextField txtDisconnected;
    protected OATextField txtComputerName;
    protected OACombo cbo;
    protected OAHtmlSelect select;
    protected OATable table;
    protected OAList list;
    
    public AppUserLoginJspBase(Hub<AppUserLogin> hub, OAForm form) {
        this(new AppUserLoginModel(hub), form, "");
    }    
    public AppUserLoginJspBase(AppUserLoginModel model, OAForm form) {
        this(model, form, "");
    }    
    public AppUserLoginJspBase(Hub<AppUserLogin> hub, OAForm form, String idPrefix) {
        this(new AppUserLoginModel(hub), form, idPrefix);
    }    
    public AppUserLoginJspBase(AppUserLoginModel model, OAForm form, String idPrefix) {
        this.model = model;
        this.form = form;
        this.idPrefix = (idPrefix == null ? "" : idPrefix);
    }    
    
    public AppUserLoginModel getModel() {
        return model;
    }
    public OAForm getForm() {
        return form;
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

    public OATextField getCreatedTextField() {
        if (txtCreated == null) {
            txtCreated = createCreatedTextField(idPrefix+JSP_txtCreated);
        }
        return txtCreated;
    }
    public OATextField createCreatedTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUserLogin.P_Created, 10, 35);
        return txt;
    }

    public OATextField getDisconnectedTextField() {
        if (txtDisconnected == null) {
            txtDisconnected = createDisconnectedTextField(idPrefix+JSP_txtDisconnected);
        }
        return txtDisconnected;
    }
    public OATextField createDisconnectedTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUserLogin.P_Disconnected, 10, 35);
        return txt;
    }

    public OATextField getComputerNameTextField() {
        if (txtComputerName == null) {
            txtComputerName = createComputerNameTextField(idPrefix+JSP_txtComputerName);
        }
        return txtComputerName;
    }
    public OATextField createComputerNameTextField(String id) {
        OATextField txt = new OATextField(id, model.getHub(), AppUserLogin.P_ComputerName, 10, 35);
        return txt;
    }
    
    public OACombo getCombo() {
        if (cbo == null) {
            cbo = createCombo(idPrefix+JSP_cbo);
        }
        return cbo;
    }
    public OACombo createCombo(String id) {
        OACombo cbo = new OACombo(id, model.getHub(), AppUserLogin.P_Created, 15);
        // cbo.setRecursive(false); 
        cbo.setNullDescription("Select an AppUserLogin");
        cbo.setAjaxSubmit(true);
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
        OATable table = new OATable(id, model.getHub());
        if (model.getAllowMultiSelect()) {
            table.setSelectHub(model.getMultiSelectHub());
        }
        table.setAjaxSubmit(true);
        table.setPager(10, 0, 5, true, true);
        addColumns(table);
        return table;
    }

    public void addColumns(OATable table) {
        OATableColumn tc = new OATableColumn(AppUserLogin.P_Created, "Created", 16);
        OATextField txt = createCreatedTextField("txtTableCreated");
        tc.setEditor(txt);
        table.addColumn(tc);

        tc = new OATableColumn(AppUserLogin.P_Disconnected, "Disconnected", 16);
        txt = createDisconnectedTextField("txtTableDisconnected");
        tc.setEditor(txt);
        table.addColumn(tc);

        tc = new OATableColumn(AppUserLogin.P_ComputerName, "Computer", 16);
        txt = createComputerNameTextField("txtTableComputerName");
        tc.setEditor(txt);
        table.addColumn(tc);
    }

    public OAList getList() {
        if (list == null) {
            list = createList(JSP_list);
        }
        return list;
    }
    public OAList createList(String id) {
        OAList list = new OAList(id, model.getHub(), AppUserLogin.P_ConnectionId, 12, 10) {
            public String getHtml(Object obj, int pos) {
                if (obj == null) return "set AO to null";
                String s = super.getHtml(obj, pos);
                s += "." + pos;
                return s;
            }
        };
        list.setRequired(true);
        return list;
    }
}



