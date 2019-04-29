package com.template.control;

import java.util.*;

import javax.swing.*;

import com.template.resource.*;
import com.template.util.*;
import com.template.view.*;

public class AboutController {
	private AboutDialog dlgAbout;

	public AboutController(JFrame frm) {
		dlgAbout = new AboutDialog(frm) {
			protected void onShowInformation(boolean bSelected) {
				if (!bSelected) {
					dlgAbout.showPanel(AboutDialog.CARD_Splash);
				}
				else {
					dlgAbout.showPanel(AboutDialog.CARD_Info);
					onRefreshInformation();
				}
			}
	        protected void onRefreshInformation() {
                dlgAbout.getTextArea().setText("refreshing information ...");
                dlgAbout.getTextArea().setCaretPosition(0);
	            SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
	                String txt;
	                @Override
	                protected Void doInBackground() throws Exception {
	                    Vector v = Util.getInfo();
	                    StringBuilder sb = new StringBuilder(4 * 1024);
	                    int x = v.size();
	                    for (int i=0; i<x; i++) {
	                        String sx = (String) v.elementAt(i);
	                        if (sx == null) continue;
	                        sx = sx.replace('\n', ' ');
	                        sx = sx.replace('\r', ' ');
	                        sb.append(sx + "\r\n");
	                    }
	                    txt = new String(sb);
	                    return null;
	                }
	                @Override
	                protected void done() {
                        dlgAbout.getTextArea().setText(txt);
                        dlgAbout.getTextArea().setCaretPosition(0);
	                }
	            };
	            sw.execute();
	        }
		};
		dlgAbout.setTitle("About " + Resource.getRunTimeName());
		
		String s = Resource.getValue(Resource.APP_Copyright, "");
		dlgAbout.getCopyrightLabel().setText(s);
	}

	
	public void setVisible(boolean b) {
	    
        String s = Resource.getRunTimeName();
        s += " Version: " + Resource.getValue(Resource.APP_Version);
        s += "." + Resource.getValue(Resource.APP_Release);
        s += " " + Resource.getValue(Resource.APP_ReleaseDate);
        // s += " " + System.getProperty("java.vm.name");
        // s += " " + System.getProperty("java.vm.version");
        s += " (Java " + System.getProperty("java.version") + ")";
        dlgAbout.getTitleLabel().setText(s);
	    
		dlgAbout.showPanel(AboutDialog.CARD_Splash);
		dlgAbout.setVisible(b);
	}
	
	public void updateUI() {
		if (dlgAbout != null) SwingUtilities.updateComponentTreeUI(dlgAbout);
	}
	public AboutDialog getAboutDialog() {
	    return dlgAbout;
	}

	public static void main(String[] args) {
        int xx = 4;
        xx++;
    }
}


