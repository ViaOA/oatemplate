package com.template.report.oa;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import com.template.control.LogController;
import com.template.control.client.PdfController;
import com.template.model.oa.AppUser;
import com.template.resource.Resource;
import com.viaoa.hub.Hub;
import com.viaoa.jfc.OAButton;
import com.viaoa.jfc.OAScroller;
import com.viaoa.jfc.editor.html.OAHTMLTextPane;
import com.viaoa.jfc.editor.html.control.OAHTMLTextPaneController;
import com.viaoa.jfc.print.PrintController;
import com.viaoa.jfc.report.OAHTMLConverter;
import com.viaoa.jfc.report.OAHTMLReport;
import com.viaoa.object.OAObject;
import com.viaoa.util.OAFile;
import com.viaoa.util.OAProperties;
import com.viaoa.util.OAString;

public class TestReport {
    private static Logger LOG = Logger.getLogger(TestReport.class.getName());
    
    public static final String REPORT_Prefix = "Report_User_";
    
    private PrintController controlPrint;

    private PdfController controlPdf;
    private JFileChooser fileChooser;
    
    private OAHTMLTextPane html;
    private JPanel panHtml; 
    
    private OAHTMLConverter htmlConverter; 

    private String titleHeader;
    private String header;
    private String footer;
    private String detail;

    private OAHTMLReport report;
    private OAProperties props;

    private Hub<AppUser> hub;

    private Window windowParent;

    
/*    qqqqqqqqqqqqqqqqqqqqqqqqq
  
    setObject
    refresh

        controlPrint.setPrintable("Estimate Report", report, report.getPageFormat());
*/    

    public TestReport(Window windowParent) {
        this.windowParent = windowParent;
    }
    
    public Hub<AppUser> getHub() {
        if (hub == null) {
            hub = new Hub<AppUser>(AppUser.class);
        }
        return hub;
    }

    
    
    public JPanel getHTMLTextPanel() {
        
        if (panHtml != null) return panHtml;
        
        panHtml = new JPanel(new BorderLayout());
        
        panHtml.add(new OAScroller(getToolBar()), BorderLayout.NORTH);
        
        panHtml.add(new JScrollPane(getHTMLTextPane()), BorderLayout.CENTER);
        return panHtml;
    }
    public OAHTMLTextPane getHTMLTextPane() {
        if (html != null) return html;
        html = new OAHTMLTextPane();
        // html.setSpellChecker(Resource.getSpellChecker());
        html.setPreferredSize(10,  4);
        // html.setEnabled(ModelDelegate.getUserAccessHub(), UserAccess.PROPERTY_IsUser);
        //html.setEditable(false);
        //qqqqqq need to create a place to have the images stored
        // html.createImageHandler(hubImageStore, ImageStore.PROPERTY_Bytes, ImageStore.PROPERTY_OrigFileName, ImageStore.PROPERTY_Id);
        return html;
    }


    public String getTitleHeader() {
        return titleHeader;
    }
    public void setTitleHeader(String html) {
        this.titleHeader = html;
        if (report != null) report.setTitleHeaderHTML(titleHeader);
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String html) {
        this.header = html;
        if (report != null) report.setHeaderHTML(header);
    }
    public String getFooter() {
        return footer;
    }
    public void setFooter(String html) {
        this.footer = html;
        if (report != null) report.setFooterHTML(footer);
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String html) {
        this.detail = html;
        if (report != null) report.setDetailHTML(detail);
    }

    public OAProperties getProperties() {
        if (props == null) {
            props = new OAProperties();
        }
        return props;
    }

    public OAHTMLReport getReport() {
        if (report == null) {
            report = new OAHTMLReport(getHTMLTextPane()) {
                @Override
                protected String getValue(String defaultValue, OAObject obj, String propertyName, int width, String fmt, OAProperties props) {
                    return TestReport.this.getValue(defaultValue, obj, propertyName, width, fmt, props);
                }
                @Override
                public OAProperties getProperties() {
                    return TestReport.this.getProperties();
                }
            };
        }
        return report;
    }
    
    public void init() {
        String titleHeader = null;
        String header = null;
        String footer = null;
        String detail = null;
        try {
            titleHeader = OAFile.readTextFile(this.getClass(), "/com/templaze/report/html/titleHeader.html", 1024);
            header = OAFile.readTextFile(this.getClass(), "/com/templaze/report/html/header.html", 1024);
            footer = OAFile.readTextFile(this.getClass(), "/com/templaze/report/html/footer.html", 1024);
            
            detail = OAFile.readTextFile(this.getClass(), "/com/templaze/report/html/oa/test.html", 1024 * 3);
        }
        catch (Exception e) {
            System.out.println("Cant read head/detail/foot.html from report directory");
        }
        setTitleHeader(titleHeader);
        setHeader(header);
        setFooter(footer);
        setDetail(detail);
        getProperties().put("Heading", "User Report");
        getProperties().put("Footing", "");
        
        getReport();
        report.setTitle("User Report");
        report.setProperties(props);
        
        report.setTitleHeaderHTML(getTitleHeader());
        report.setHeaderHTML(getHeader());
        report.setFooterHTML(getFooter());
        report.setDetailHTML(getDetail());
        
        
        // set up page settings
        //PageFormat pageFormat = report.getPageFormat();
        //loadPageFormat(pageFormat);
        
        // pageFormat = controlPrint.validate(pageFormat);
        //report.setPageFormat(pageFormat);
        
//        report.setHub(getHub());
        
        AppUser user = new AppUser();
        user.setLastName("LastNameTest");
        report.setObject(user);
//qqqqqqqqq        
        getPrintController().setPrintable(getReport());
    }
    
    
    protected String getValue(String defaultValue, OAObject obj, String propertyName, int width, String fmt, OAProperties props) {
        return defaultValue;
    }    
    protected Object getProperty(Object defaultValue, OAObject oaObj, String propertyName) {
        return defaultValue;
    }
    
//qqqqqqqqqq use getValue and getProperty instead    
    public OAHTMLConverter getHTMLConverter() {
        if (htmlConverter != null) return htmlConverter;
        htmlConverter = new OAHTMLConverter() {
            @Override
            protected String getValue(OAObject obj, String propertyName, int width, String fmt, OAProperties props,  boolean bx) {
                if (obj == null) return "";
                boolean b = (propertyName != null && propertyName.startsWith("split$"));
                String s = "";
                /*
                if ("count$".equals(propertyName) && obj instanceof SalesOrderItem) {
                    SalesOrderItem soi = (SalesOrderItem) obj;
                    SalesOrder so = soi.getSalesOrder();
                    if (so == null) return "?";
                    int pos = soi.getSalesOrder().getSalesOrderItems().getPos(soi);
                    if (pos < 0) return "?";
                    return (pos+1)+"";
                }
                if (b) propertyName = propertyName.substring(6);
                String s = super.getValue(obj, propertyName, width, fmt, props);
                if (b) {
                    SalesOrder so = ((SalesOrderItem) obj).getSalesOrder();
                    if (so != null) {
                        Hub<SalesOrderItem> h = so.getSalesOrderItems();
                        int max = 35;
                        / *
                        for (SalesOrderItem soi : h) {
                            if (OAString.isEmpty(soi.getNote())) {
                                max = 17;
                            }
                        }
                        * /
                        s = OAString.lineBreak(s, max, "<br>", 0);
                    }
                }
                */
                return s;
            }
        };
        return htmlConverter;
    }
    
    protected void loadPageFormat(PageFormat pageFormat) {
        Paper paper = pageFormat.getPaper();  // this creates a copy of paper

        double w = Resource.getDouble(REPORT_Prefix+"PaperWidth", 0.0);
        double h = Resource.getDouble(REPORT_Prefix+"PaperHeight", 0.0);
        if (w > 1.0 && h > 1.0) {
            paper.setSize(w,h);
        }
        else {
            w = paper.getWidth();
            h = paper.getHeight();
        }

        double x = Resource.getDouble(REPORT_Prefix+"X", 18);
        double y = Resource.getDouble(REPORT_Prefix+"Y", 18);
        double w2 = Resource.getDouble(REPORT_Prefix+"Width");
        double h2 = Resource.getDouble(REPORT_Prefix+"Height");
        if (w2 == 0.0) w2 = w - (x * 2);
        if (h2 == 0.0) h2 = h - (y * 2);
        paper.setImageableArea(x,y,w,h);
        pageFormat.setPaper(paper);
            
        String s = Resource.getValue(REPORT_Prefix+"Orientation");
        if (s != null) {
            if (s.equalsIgnoreCase("Landscape")) pageFormat.setOrientation(PageFormat.LANDSCAPE);
            else pageFormat.setOrientation(PageFormat.PORTRAIT);
        }
    }

/*    
    protected void savePageFormat(OAHTMLReport report) {
        PageFormat pageFormat = report.getPageFormat();
        Paper paper = pageFormat.getPaper();
        
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"PaperWidth", paper.getWidth()+"");
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"PaperHeight", paper.getHeight()+"");
        
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"X", paper.getImageableX()+"");
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"Y", paper.getImageableY()+"");
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"Width", paper.getImageableWidth()+"");
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"Height", paper.getImageableHeight()+"");

        int x = pageFormat.getOrientation();
        String s;
        if (x == PageFormat.LANDSCAPE) s = "LANDSCAPE";
        else s = "PORTRAIT";
        Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"Orientation", s);
        Resource.save();
    }
*/
    public PdfController getPdfController() {
        if (controlPdf != null) return controlPdf;
        controlPdf = new PdfController();
        return controlPdf;
    }
    
    
    public void onSaveAsPdf() {
        /*qqqqqq
        final User user = hub.getAO();
        if (user == null) return;
        final String userName = user == null ? "" : user.getFullName();
        */
        
        int x = getFileChooser().showSaveDialog(windowParent);
        if (x != JFileChooser.APPROVE_OPTION) return;
        final File file = getFileChooser().getSelectedFile();
        final String fileName = file.getPath();

        try {
            file.createNewFile();
        }
        catch (Exception e) {
            String s = "";
            for (int i=0; i<fileName.length(); i++) {
                char ch = fileName.charAt(i);
                if (Character.isDigit(ch) || Character.isLetter(ch)) continue;
                if ("\\ _-.".indexOf(ch) >= 0) continue;
                if (ch == ':' && i == 1) continue;
                if (s.length() == 0) s = "\nThe following could be the bad characters: ";
                s += ch + " ";
            }
            JOptionPane.showMessageDialog(windowParent, 
                    "Invalid/bad file name: "+fileName+"\nPlease remove any characters that are not valid,\nand try again."+s, 
                    "Save as Pdf", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        SwingWorker<Boolean, String> sw = new SwingWorker<Boolean, String>() {
            boolean bValid;
            Exception ex;
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    getPdfController().saveToPdf(getPrintController().getPrintable(), getPrintController().getPageFormat(), fileName, "Templaze Report", "");
                    bValid = true;
                }
                catch (Exception e) {
                    bValid = false;
                    ex = e;
                    LOG.log(Level.WARNING, "Exception calling saveToPdf", e);
                }
                return true;
            }
           
            @Override
            protected void done() {
                setStatus("");
                setProcessing(false);
                if (!bValid) {
                    JOptionPane.showMessageDialog(windowParent, 
                            "Could not save file\n"+ex.getMessage()+"\nPlease verify that a valid file name is used.", 
                            "Save as Pdf", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    try {
                        URL url = getClass().getResource(Resource.getJarImageDirectory() + "/" + Resource.getValue(Resource.IMG_PdfBig));
                        ImageIcon icon = new ImageIcon(url);
                        int x = JOptionPane.showConfirmDialog(windowParent, " saved as Pdf file \""+ file.getName()+"\"\nWould you like to view the Pdf document?", Resource.getValue(Resource.APP_ApplicationName), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
                        if (x == JOptionPane.YES_OPTION) {
                            Desktop.getDesktop().open(file);
                        }
                    }
                    catch (Exception e) {
                        LOG.log(LogController.Level_ERROR, "Error saving Order as Pdf file", e);
                }
                }
            }
        };
        setStatus("Saving as Pdf file \""+ file.getName()+"\" ...");
        setProcessing(true, "Saving ...");
        sw.execute();
    }

    private JButton cmdPdf;
    public JButton getPdfCommand() {
        if (cmdPdf != null) return cmdPdf;
        cmdPdf = new JButton("Save as Pdf ...");
        cmdPdf.setIcon(Resource.getJarIcon(Resource.getValue(Resource.IMG_Pdf)));
        cmdPdf.setToolTipText("Save as a Pdf a file.");
        //cmdPdf.setActionCommand(CMD_SaveAsPdf);
        cmdPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveAsPdf();
            }
        });
        OAButton.setup(cmdPdf);
        return cmdPdf;
    }
    
    private JToolBar toolBar;
    public JToolBar getToolBar() {
        if (toolBar != null) return toolBar;

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        PrintController controlPrint = getPrintController();
        toolBar.add(controlPrint.getPrintButton());
        toolBar.add(controlPrint.getPrintPreviewButton());
      
        toolBar.add(getPdfCommand());

        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(4));
        
        getHTMLTextPane().setImageLoader(getClass(), "/com/viaoa/scheduler/report/sales");
        
        OAHTMLTextPaneController controlEditor = getHTMLTextPane().getController();
        toolBar.add(controlEditor.getFontNameComboBox());
        toolBar.add(controlEditor.getFontSizeComboBox());
        toolBar.add(Box.createHorizontalStrut(4));
        toolBar.add(controlEditor.getBoldToggleButton());
        toolBar.add(controlEditor.getItalicToggleButton());
        toolBar.add(controlEditor.getUnderlineToggleButton());
        toolBar.add(Box.createHorizontalStrut(4));
        toolBar.add(controlEditor.getFontColorButton());
        toolBar.add(controlEditor.getBackgroundColorButton());
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(controlEditor.getEditSourceButton());

        return toolBar;
    }

    public PrintController getPrintController() {
        if (controlPrint != null) return controlPrint;
        controlPrint = new PrintController(windowParent) {
            @Override
            public void setPrintable(Printable printable) {
                super.setPrintable(printable);
                boolean b = (printable != null);
                
                getPdfCommand().setEnabled(b);
                getPrintController().getPrintButton().setEnabled(b);
                getPrintController().getPrintMenuItem().setEnabled(b);
                getPrintController().getPrintPreviewButton().setEnabled(b);
                getPrintController().getPrintPreviewMenuItem().setEnabled(b);
                getPrintController().getPageSetupMenuItem().setEnabled(b);
                getPrintController().getSaveAsPdfMenuItem().setEnabled(b);
            }
            @Override
            public void setPageFormat(PageFormat pf) {
/*qqq                
                if (pf != report.getPageFormat()) {
                    report.setPageFormat(pf);
                    TestReport.this.savePageFormat(report);
                }
*/                
                super.setPageFormat(pf);
            }
        };
        return controlPrint;
    }
    
    protected JFileChooser getFileChooser() {
        if (fileChooser != null) return fileChooser;
        
        fileChooser = new JFileChooser() {
            public @Override void approveSelection() {
                File f = getSelectedFile();
                String fileName = f.getPath();
                if (!fileName.endsWith(".pdf")) {
                    if (fileName.indexOf('.') < 0) {
                        fileName += ".pdf";
                        f = new File(fileName);
                        setSelectedFile(f);
                    }
                    else {
                        JOptionPane.showMessageDialog(getTopLevelAncestor(),
                                "File name must end in \".pdf\"",
                                Resource.getValue(Resource.APP_ApplicationName),
                                JOptionPane.ERROR_MESSAGE);                                    
                        return;
                    }
                }

                
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(getTopLevelAncestor(),
                            "The selected file already exists. " +
                            "Do you want to overwrite it?",
                            "The file already exists",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    switch(result)  {
                    case JOptionPane.YES_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                    }
                }
                Resource.setValue(Resource.TYPE_Client, REPORT_Prefix+"PdfFileName", fileName);
                super.approveSelection();
            }
        };
        
        javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {  // controls files that can be selected
            public boolean accept(File f) {
                if ( f.getName().toLowerCase().endsWith(".pdf") ) {
                    return true;
                }
                if ( f.isDirectory() ) return true;
                return false;
            }
            public String getDescription() {
                return "*.pdf";
            }
        };
        
        String fname = Resource.getValue(REPORT_Prefix+"PdfFileName", REPORT_Prefix+"1.pdf");
        if (!OAString.isEmpty(fname)) {
            fileChooser.setSelectedFile(new File(fname));
        }
        
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle(Resource.getValue(Resource.APP_ApplicationName));
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileHidingEnabled(false);
        // fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);

        return fileChooser;
    }
    
    public void setStatus(String msg) {
    }
    public void setProcessing(boolean b) {
    }
    public void setProcessing(boolean b, String msg) {
    }

    public static void main(String[] args) throws Exception {
        //qqqqq put this in editor startup also qqqqqq
        com.viaoa.jfc.editor.html.protocol.classpath.Handler.register();

        JFrame frm = new JFrame();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setBounds(new Rectangle(200, 200, 600, 400));
        frm.setVisible(true);
        
        TestReport ur = new TestReport(frm);
        ur.init();
        
        OAHTMLReport rpt = ur.getReport();
        rpt.refreshDetail();

        frm.add(ur.getHTMLTextPanel());
    }

//qqqqqqqqq         editor.setImageLoader(getClass(), "/com/viaoa/scheduler/report/sales");
}

