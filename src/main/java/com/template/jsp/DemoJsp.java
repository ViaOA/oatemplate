package com.template.jsp;

import java.awt.Dimension;
import java.util.*;

import com.template.jsp.oa.*;
import com.template.model.*;
import com.template.model.oa.*;
import com.viaoa.hub.*;
import com.viaoa.jsp.*;
import com.viaoa.object.*;
import com.viaoa.process.OAProcess;
import com.viaoa.util.*;

public class DemoJsp extends AppUserJspBase {

    public static final String JSP_cmdCreateOAProcess = "cmdCreateOAProcess";
    public static final String JSP_cmdHideOAProcessDialog = "cmdHideOAProcessDialog";
    public static final String JSP_cmdShowOAProcessDialog = "cmdShowOAProcessDialog";
    public static final String JSP_cmdCancelOAProcesses = "cmdCancelOAProcesses";
    public static final String JSP_cmdClearOAProcesses = "cmdClearOAProcesses";
    public static final String JSP_badgeId = "badgeId";
    public static final String JSP_popupDiv1 = "popupDiv1";
    public static final String JSP_popupDiv2 = "popupDiv2";
    public static final String JSP_popupDiv3 = "popupDiv3";
    public static final String JSP_popupDiv4 = "popupDiv4";
    public static final String JSP_popupDiv5 = "popupDiv5";
    public static final String JSP_popupDiv6 = "popupDiv6";
    public static final String JSP_popupDiv7 = "popupDiv7";
    public static final String JSP_popupDivA = "popupDivA";
    public static final String JSP_popupDivB = "popupDivB";
    public static final String JSP_expanderDiv = "expanderDiv";
    public static final String JSP_txtFirstNameAjax = "txtFirstNameAjax";
    public static final String JSP_lblFullNameAjax = "lblFullNameAjax";
    public static final String JSP_radAdminTrue = "radAdminTrue";
    public static final String JSP_radAdminFalse = "radAdminFalse";
    public static final String JSP_cmdSubmit = "cmdSubmit";
    public static final String JSP_cmdSubmitAjax = "cmdSubmitAjax";
    public static final String JSP_lblTest = "lblTest";
    public static final String JSP_txtaLastName = "txtLastName";
    public static final String JSP_listing = "listing";
    public static final String JSP_cmdButtonList1 = "cmdButtonList1";
    public static final String JSP_cmdButtonList2 = "cmdButtonList2";
    public static final String JSP_cmdButtonList3 = "cmdButtonList3";
    public static final String JSP_cmdButtonList4 = "cmdButtonList4";
    public static final String JSP_cmdSnackbar = "cmdSnackbar";
    public static final String JSP_dlgTest = "dlgTest";
    public static final String JSP_tree = "tree";
    public static final String JSP_spanAaa = "spanAaa";

    protected OAButton cmdCreateOAProcess;
    protected OAButton cmdHideOAProcessDialog;
    protected OAButton cmdShowOAProcessDialog;
    protected OAButton cmdCancelOAProcesses;
    protected OAButton cmdClearOAProcesses;

    protected OABadge badgeId;
    protected OAPopup popupDiv1;
    protected OAPopup popupDiv2;
    protected OAPopup popupDiv3;
    protected OAPopup popupDiv4;
    protected OAPopup popupDiv5;
    protected OAPopup popupDiv6;
    protected OAPopup popupDiv7;
    protected OAPopup popupDivA;
    protected OAPopup popupDivB;

    protected OAExpander expanderDiv;
    protected OATextField txtFirstNameAjax;
    protected OALabel lblFullNameAjax;

    protected OARadio radAdminTrue;
    protected OARadio radAdminFalse;
    protected OAButton cmdSubmit;
    protected OAButton cmdSnackbar;
    protected OAButton cmdSubmitAjax;
    protected OALabel lblTest;
    protected OATextArea txtaLastName;
    protected OAListing listing;
    protected OAButtonList cmdButtonList1;
    protected OAButtonList cmdButtonList2;
    protected OAButtonList cmdButtonList3;
    protected OAButtonList cmdButtonList4;
    protected OADialog dlgTest;
    protected OAPopover popoverTest;
    protected OASlimscroll slimscrollTest;
    protected OAStyledTextArea styleTest;

    protected OATree tree;
    
    
    public DemoJsp(Hub<AppUser> hub, OAForm form) {
        this(new AppUserModel(hub), form);
    }

    public DemoJsp(AppUserModel model, OAForm form) {
        super(model, form);
        setup();
    }

    protected void setup() {
        form.add(getIdLabel());
        form.add(getFirstNameTextField());
        form.add(getLastNameTextField());
        form.add(getPasswordPassword());
        form.add(getAdminCheckBox());
        form.add(getInactiveDateTextField());
        form.add(getCombo());

        form.add(getCreateOAProcessCommand());
        form.add(getHideOAProcessDialogCommand());
        form.add(getShowOAProcessDialogCommand());
        form.add(getCancelOAProcessesCommand());
        form.add(getClearOAProcessesCommand());
        form.add(getIdBadge());
        form.add(getDiv1Popup());
        form.add(getDiv2Popup());
        form.add(getDiv3Popup());
        form.add(getDiv4Popup());
        form.add(getDiv5Popup());
        form.add(getDiv6Popup());
        form.add(getDiv7Popup());
        form.add(getDivAPopup());
        form.add(getDivBPopup());
        form.add(getFirstNameAjaxTextField());
        form.add(getFullNameAjaxLabel());
        form.add(getAdminTrueRadio());
        form.add(getAdminFalseRadio());

        form.add(getAppUserLoginsJsp().getCreatedTextField());
        form.add(getAppUserLoginsJsp().getCombo());
        form.add(getSelect());
        form.add(getSubmitButton());
        form.add(getSubmitAjaxButton());
        form.add(getTestLabel());
        form.add(getLastNameTextArea());
        form.add(getTable());
        form.add(getAppUserLoginsJsp().createTable("tableAppUserLogins"));
        form.add(getList());
        form.add(getButtonList1());
        form.add(getButtonList2());
        form.add(getButtonList3());
        form.add(getButtonList4());
        form.add(getGrid());
        form.add(getSnackbarButton());
        form.add(getTestDialog());
        form.add(getDivExpander());
        form.add(getTree());
        form.add(getAppUserLoginsJsp().createList("listAppUserLogins"));
        form.add(getPopoverTest());
        form.add(getSlimscrollTest());
        form.add(getStyledTextAreaTest());
        
        OAHtmlElement ele = new OAHtmlElement(JSP_spanAaa);
        ele.addStyle("font-size", "8pt");
        ele.addStyle("color", "green");
        form.add(ele);
    }

    public OAButton getSubmitButton() {
        if (cmdSubmit == null) {
            cmdSubmit = new OAButton(JSP_cmdSubmit, model.getHub()) {
                public String onSubmit(String forwardUrl) {
                    // add any valid javascript to be sent to browser
                    getForm().addScript("alert('cmdSubmit clicked');");
                    return null;
                }
            };
            cmdSubmit.setToolTip("this is tooltip, current user is <%=fullname%>");
        }
        return cmdSubmit;
    }

    public OAButton getSubmitAjaxButton() {
        if (cmdSubmitAjax == null) {
            cmdSubmitAjax = new OAButton(JSP_cmdSubmitAjax, model.getHub()) {
                public String onSubmit(String forwardUrl) {
                    // add any valid javascript to be sent to browser
                    getForm().addScript("alert('cmdAjaxSubmit clicked');");
                    return null;
                }
            };
            cmdSubmitAjax.setAjaxSubmit(true);
            cmdSubmitAjax.setSpinner(true);
        }
        return cmdSubmitAjax;
    }

    public OAButton getCreateOAProcessCommand() {
        if (cmdCreateOAProcess != null) return cmdCreateOAProcess;
        cmdCreateOAProcess = new OAButton(JSP_cmdCreateOAProcess) {
            int cnt;

            public String onSubmit(String forwardUrl) {
                cnt++;
                OAProcess proc = new OAProcess() {
                    public void run() {
                        for (int i = 0;; i++) {
                            if (wasCancelled()) break;
                            setCurrentStep((i % 3) + 1);
                            try {
                                Thread.sleep(2500);
                            }
                            catch (Exception e) {
                                this.setException(e);
                                this.cancel("exception");
                            }
                        }
                        this.done();
                    }
                };
                proc.setName("unblocking test process number " + cnt);
                proc.setDescription("test description for the processes number " + cnt);
                proc.setBlock(false);
                proc.setSteps("this is #" + cnt + " step1", "this is #" + cnt + " step2", "this is #" + cnt + " step3");

                (new Thread(proc)).start();
                form.addProcess(proc);
                getForm().addSnackbarMessage("created new process " + cnt);

                return null;
            }
        };
        cmdCreateOAProcess.setAjaxSubmit(true);
        //qqqqqqqqqqqqqq        
        if (form != null) form.add(cmdCreateOAProcess);
        return cmdCreateOAProcess;
    }

    public OAButton getHideOAProcessDialogCommand() {
        if (cmdHideOAProcessDialog != null) return cmdHideOAProcessDialog;
        cmdHideOAProcessDialog = new OAButton(JSP_cmdHideOAProcessDialog) {
            public String onSubmit(String forwardUrl) {
                getForm().showProcesses(false);
                getForm().addSnackbarMessage("hide process dialog");
                return null;
            }
        };
        cmdHideOAProcessDialog.setAjaxSubmit(true);
        return cmdHideOAProcessDialog;
    }

    public OAButton getShowOAProcessDialogCommand() {
        if (cmdShowOAProcessDialog != null) return cmdShowOAProcessDialog;
        cmdShowOAProcessDialog = new OAButton(JSP_cmdShowOAProcessDialog) {
            public String onSubmit(String forwardUrl) {
                getForm().showProcesses(true);
                getForm().addSnackbarMessage("show process dialog");
                return null;
            }
        };
        cmdShowOAProcessDialog.setAjaxSubmit(true);
        return cmdShowOAProcessDialog;
    }

    public OAButton getCancelOAProcessesCommand() {
        if (cmdCancelOAProcesses != null) return cmdCancelOAProcesses;
        cmdCancelOAProcesses = new OAButton(JSP_cmdCancelOAProcesses) {
            public String onSubmit(String forwardUrl) {
                for (OAProcess ps : getForm().getProcesses()) {
                    ps.cancel("cancelled by user");
                }
                getForm().showProcesses(false);
                getForm().addSnackbarMessage("cancelled processes");
                return null;
            }
        };
        cmdCancelOAProcesses.setAjaxSubmit(true);
        return cmdCancelOAProcesses;
    }

    public OAButton getClearOAProcessesCommand() {
        if (cmdClearOAProcesses != null) return cmdClearOAProcesses;
        cmdClearOAProcesses = new OAButton(JSP_cmdClearOAProcesses) {
            public String onSubmit(String forwardUrl) {
                getForm().clearProcesses();
                getForm().addSnackbarMessage("cleared processes from Form");
                return null;
            }
        };
        cmdClearOAProcesses.setAjaxSubmit(true);
        return cmdClearOAProcesses;
    }

    public OABadge getIdBadge() {
        if (badgeId != null) return badgeId;
        badgeId = new OABadge(JSP_badgeId, model.getHub(), AppUser.P_Id);
        return badgeId;
    }

    public OAPopup getDiv1Popup() {
        if (popupDiv1 != null) return popupDiv1;
        popupDiv1 = new OAPopup(JSP_popupDiv1, "popupCommand1", "0", "0", "", "");
        popupDiv1.setMaxHeight("200px");
        popupDiv1.setMaxWidth("200px");
        popupDiv1.setOverflow("scroll");
        return popupDiv1;
    }

    public OAPopup getDiv2Popup() {
        if (popupDiv2 != null) return popupDiv2;
        popupDiv2 = new OAPopup(JSP_popupDiv2, "popupCommand2", "", "0", "0", "");
        popupDiv2.setMaxHeight("200px");
        popupDiv2.setMaxWidth("200px");
        popupDiv2.setOverflow("scroll");
        return popupDiv2;
    }

    public OAPopup getDiv3Popup() {
        if (popupDiv3 != null) return popupDiv3;
        popupDiv3 = new OAPopup(JSP_popupDiv3, "popupCommand3", "", "", "0", "0");
        popupDiv3.setHtml("popup bottom left<h1>this is a 600x600 minimum");
        popupDiv3.setMinHeight("600px");
        popupDiv3.setMinWidth("600px");
        popupDiv3.setOverflow("hidden");
        return popupDiv3;
    }

    public OAPopup getDiv4Popup() {
        if (popupDiv4 != null) return popupDiv4;
        popupDiv4 = new OAPopup(JSP_popupDiv4, "popupCommand4", "0", "", "", "0");
        return popupDiv4;
    }

    public OAPopup getDiv5Popup() {
        if (popupDiv5 != null) return popupDiv5;
        popupDiv5 = new OAPopup(JSP_popupDiv5, "popupCommand5");
        return popupDiv5;
    }

    public OAPopup getDiv6Popup() {
        if (popupDiv6 != null) return popupDiv6;
        popupDiv6 = new OAPopup(JSP_popupDiv6, "popupCommand6", "30", "", "", "44");
        return popupDiv6;
    }

    public OAPopup getDiv7Popup() {
        if (popupDiv7 != null) return popupDiv7;
        popupDiv7 = new OAPopup(JSP_popupDiv7, "popupCommand7", "50vh", "0", "", "");
        return popupDiv7;
    }

    public OAPopup getDivAPopup() {
        if (popupDivA != null) return popupDivA;
        popupDivA = new OAPopup(JSP_popupDivA, "popupCommandA", "0", "", "", "50vw");
        return popupDivA;
    }

    public OAPopup getDivBPopup() {
        if (popupDivB != null) return popupDivB;
        popupDivB = new OAPopup(JSP_popupDivB, "popupCommandB", "", "", "0", "50vw");
        return popupDivB;
    }

    public OAExpander getDivExpander() {
        if (expanderDiv != null) return expanderDiv;
        expanderDiv = new OAExpander(JSP_expanderDiv, "expanderCommand", "click to Expand", "click to Collapse");
        return expanderDiv;
    }

    public OATextField getFirstNameAjaxTextField() {
        if (txtFirstNameAjax != null) return txtFirstNameAjax;
        txtFirstNameAjax = new OATextField(JSP_txtFirstNameAjax, model.getHub(), AppUser.P_FirstName, 10, 25);
        txtFirstNameAjax.setAjaxSubmit(true);
        // txtFirstNameAjax.setToolTip("my full name is <%=fullname%>");
        txtFirstNameAjax.setFloatLabel("the first name");
        txtFirstNameAjax.setPlaceholder("First Name");
        return txtFirstNameAjax;
    }

    public OALabel getFullNameAjaxLabel() {
        if (lblFullNameAjax != null) return lblFullNameAjax;
        lblFullNameAjax = new OALabel(JSP_lblFullNameAjax, model.getHub(), AppUser.P_FullName, 35) {
            public String getValue(String value) {
                return value;
            }

            public String onSubmit(String forwardUrl) {
                // add any valid javascript to be sent to browser
                //getForm().addScript("alert('lblFullName submitted form using ajax');");

                // add messages to this form
                getForm().addMessage("form: LABEL was selected");
                getForm().addError("form: LABEL was selected");
                getForm().addHiddenMessage("form: LABEL was selected");

                // or add it to the session, and have it show up on this users next page that is displayed
                getForm().getSession().addMessage("session: LABEL was selected");
                getForm().getSession().addError("session: LABEL was selected");
                getForm().getSession().addHiddenMessage("session: LABEL was selected");

                OADialog dlg = getForm().getDialog("dlgTest");
                dlg.show();

                getForm().addPopupMessage("form popup message is here");

                getForm().addSnackbarMessage("form snackbar message is here " + (new OADateTime()));
                return forwardUrl;
            }
        };
        lblFullNameAjax.setMinLineWidth(25);
        lblFullNameAjax.setAjaxSubmit(true);
        // lbl.setSubmit(true);
        // lbl.setForwardUrl("xxx");
        return lblFullNameAjax;
    }

    public OARadio getAdminTrueRadio() {
        if (radAdminTrue != null) return radAdminTrue;
        radAdminTrue = new OARadio(JSP_radAdminTrue, "radAdmin", model.getHub(), AppUser.P_Admin, true);
        return radAdminTrue;
    }

    public OARadio getAdminFalseRadio() {
        if (radAdminFalse != null) return radAdminTrue;
        radAdminFalse = new OARadio(JSP_radAdminTrue, "radAdmin", model.getHub(), AppUser.P_Admin, false);
        return radAdminFalse;
    }

    public OALabel getTestLabel() {
        if (lblTest != null) return lblTest;
        lblTest = new OALabel(JSP_lblTest, model.getHub(), AppUser.P_FullName, 35) {
            public String getValue(String value) {
                return value;
            }

            public String onSubmit(String forwardUrl) {
                // add any valid javascript to be sent to browser
                // getForm().addScript("alert('lblTest submitted form using ajax');");

                // add messages to this form
                getForm().addMessage("form: LABEL was selected");
                getForm().addError("form: LABEL was selected");
                getForm().addHiddenMessage("form: LABEL was selected");

                // or add it to the session, and have it show up on this users next page that is displayed
                getForm().getSession().addMessage("session: LABEL was selected");
                getForm().getSession().addError("session: LABEL was selected");
                getForm().getSession().addHiddenMessage("session: LABEL was selected");

                return forwardUrl;
            }
        };
        lblTest.setMinLineWidth(25);
        lblTest.setAjaxSubmit(true);
        // lblTest.setSubmit(true);
        // lblTest.setForwardUrl("xxx");
        return lblTest;
    }
    
    
    public OATextArea getLastNameTextArea() {
        if (txtaLastName == null) {
            txtaLastName = new OATextArea("txta", model.getHub(), AppUser.P_LastName, 15, 6, 25);
            // txta.setAjaxSubmit(true);
        }
        return txtaLastName;
    }


    public OAListing getListing() {
        if (listing != null) return listing;
        listing = new OAListing("listing", model.getHub(), AppUser.P_FullName) {
            public String getHtml(Object obj, int pos) {
                if (obj == null) return "set AO to null";
                String s = super.getHtml(obj, pos);
                s += "*" + pos;
                return s;
            }
        };
        listing.setFormat("12L.");
        return listing;
    }
    
    public OAButtonList getButtonList1() {
        if (cmdButtonList1 != null) return cmdButtonList1;
        cmdButtonList1 = new OAButtonList(JSP_cmdButtonList1, model.getHub(), AppUser.P_FullName);
        return cmdButtonList1;
    }
    
    
    public OAButtonList getButtonList2() {
        if (cmdButtonList2 != null) return cmdButtonList2;
        cmdButtonList2 = new OAButtonList(JSP_cmdButtonList2, model.getHub(), AppUser.P_FullName, 20, 12);
        // cmdButtonList2.setPopupColumns(28);
        cmdButtonList2.addHeading(0, "Top Performer Group");
        cmdButtonList2.addHeading(3, "The Other Group");
        return cmdButtonList2;
    }

    public OAButtonList getButtonList3() {
        if (cmdButtonList3 != null) return cmdButtonList3;
        cmdButtonList3 = new OAButtonList(JSP_cmdButtonList3, model.getHub(), AppUser.P_FullName) {// , 12, 14) {
            @Override
            public String getHtml(Object obj, int pos) {
                if (obj == null) return "<i style='padding-left: 20px; border-left: 2px blue solid;'>Select a user from the list</i>";
                return super.getHtml(obj, pos);
            }
        };
        // cmdButtonList3.setPopupColumns(32);
        cmdButtonList3.setHeadingPropertyPath(AppUser.P_LastName);
        cmdButtonList3.setHtmlTemplate("<%=$OAROW%> <%=fullName%> - <%=note%> <small>(<%=id%>)</small>");
        cmdButtonList3.setNullDescription("Select a user");
        cmdButtonList3.setToolTipHtmlTemplate("User Id: <%=id%>");
        cmdButtonList3.setToolTip("Select an employee from the list");
        return cmdButtonList3;
    }

    public OAButtonList getButtonList4() {
        if (cmdButtonList4 != null) return cmdButtonList4;
        cmdButtonList4 = new OAButtonList(JSP_cmdButtonList4, model.getHub(), AppUser.P_FullName, "30%", "441px") {
            @Override
            public String getHtml(Object obj, int pos) {
                if (obj == null) return "<i style='padding-left: 20px; border-left: 2px blue solid;'>Select a user from the list</i>";
                return super.getHtml(obj, pos);
            }
        };
        // cmdButtonList4.setPopupColumns(82);
        cmdButtonList4.setPopupWidth("100%");
        cmdButtonList4.setHeadingPropertyPath(AppUser.P_LastName);
        cmdButtonList4.setHtmlTemplate("<%=$OAROW%> <%=fullName%> - <%=note%> <small>(<%=id%>)</small>");
        cmdButtonList4.setNullDescription("Select a user");
        //cmdButtonList4.setToolTipHtmlTemplate("User Id: <%=id%>");
        cmdButtonList4.setToolTip("Select an employee from the list");
        return cmdButtonList4;
    }
    
    
    public OAButton getSnackbarButton() {
        if (cmdSnackbar != null) return cmdSnackbar;
        cmdSnackbar = new OAButton(JSP_cmdSnackbar, model.getHub()) {
            public String onSubmit(String forwardUrl) {
                getForm().addSnackbarMessage("snackbar message goes here, " + (new OATime()));
                return null;
            }
        };
        cmdSnackbar.setAjaxSubmit(true);
        return cmdSnackbar;
    }

    public OADialog getTestDialog() {
        if (dlgTest != null) return dlgTest;
        
        dlgTest = new OADialog(JSP_dlgTest) {
            public String onSubmit(String forwardUrl, String submitButtonText) {
                getForm().addMessage("submitButtonText ==>" + submitButtonText);
                // System.out.println("submitButtonText ==>"+submitButtonText);
                return forwardUrl;
            }
        };
        dlgTest.setCloseButtonText("button to close");
        dlgTest.setTitle("Title text goes here");
        dlgTest.setModal(true);
        dlgTest.setDimension(new Dimension(450, 350));
        dlgTest.addButton("button1");
        dlgTest.addButton("button2");
        dlgTest.addButton("button3");
        dlgTest.setAjaxSubmit(true);
        
        return dlgTest;
    }

    public OATree getTree() {
        if (tree != null) return tree;

        tree = new OATree(JSP_tree, model.getHub(), AppUser.P_FullName) {
            @Override
            public String onSubmit(String forwardUrl) {
                return forwardUrl;
            }
        };
        tree.setAjaxSubmit(true);
        tree.setSortBy(AppUser.P_FullName);
        //tree.setTreeViewParams("showBorder: false, selectedColor: 'black', selectedBackColor: 'white'");
        tree.setTreeViewParams("showTags: true");
        // tree.setFilter(new OAFilter<ItemCategory>() {
        return tree;
    }
    

    public OAPopover getPopoverTest() {
        if (popoverTest != null) return popoverTest;
        popoverTest = new OAPopover("cmdPopover", model.getHub());
        popoverTest.setTitle("Popover test");
        popoverTest.setMessage("This is a popover message for user <%=fullname%>");
        popoverTest.setOnHover(true);
        return popoverTest;
    }
    
    public OASlimscroll getSlimscrollTest() {
        if (slimscrollTest != null) return slimscrollTest;
        slimscrollTest = new OASlimscroll("slimscrollTest", "350px", "90px");
        return slimscrollTest;
    }
    public OAStyledTextArea getStyledTextAreaTest() {
        if (styleTest != null) return styleTest;
        styleTest = new OAStyledTextArea("stxtaTest", model.getHub(), AppUser.P_Note, 0, 12);
        return styleTest;
    }
    
    public OAGrid getGrid() {
        if (grid == null) {
            grid = createGrid(JSP_grid);
        }
        return grid;
    }
    public OAGrid createGrid(String id) {
        grid = new OAGrid(id, model.getHub(), 4);
        grid.setHtmlTemplate("<div style='border: 5px green solid; padding: 12px; height: 100px; width: 200px; overflow: hidden;'><nobr>Fullname is <%=fullName%></nobr><br>button <%=button%></div>");
        grid.setAjaxSubmit(true);
        grid.setPager(5, 0, 7, true, true);

        OAButton cmd = new OAButton("button") {
            public String onSubmit(String forwardUrl) {
                // add any valid javascript to be sent to browser
                getForm().addScript("alert('grid button clicked, hub.pos="+model.getHub().getPos()+"');");
                return forwardUrl;
            }
            @Override
            public String getRenderText(OAObject obj) {
                return "click me";
            }
        };
        grid.add(cmd);
        
        return grid;
    }
    

    
//qqqq sent to Jason    
private OAButton cmdRunProcess;
public OAButton getRunProcess() {
    if (cmdRunProcess != null) return cmdRunProcess;
    cmdRunProcess = new OAButton("cmdRunProcess") {
        public String onSubmit(String forwardUrl) {
            OAProcess proc = new OAProcess() {
                public void run() {

                    // ... do your processing code
                    
                    setCurrentStep(1);  // tell UI which step you are on 
                    // .. 
                    if (wasCancelled()) return;  // check to see if user cancelled 
                    setCurrentStep(2);
                    // ..
                    done();
                }
            };
            proc.setName("run process xzy");
            proc.setDescription("description here");
            proc.setBlock(false);  // should page be blocked
            proc.setSteps("step1", "step2", "step3");  // title/msg for each step, to show the user

            (new Thread(proc)).start();  // run in another thread
            
            form.addProcess(proc);
            form.addSnackbarMessage("started new process");

            return null;
        }
    };
    cmdCreateOAProcess.setAjaxSubmit(true);
    if (form != null) form.add(cmdCreateOAProcess);
    return cmdRunProcess;
}

}


