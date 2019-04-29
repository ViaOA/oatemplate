package com.template.view;

import java.net.URL;

import javax.swing.*;

import com.template.resource.Resource;

/**
 *  This is used as a base Frame for dialogs that
 *  are used before the main Frame is displayed.
 */
public class DummyFrame extends JFrame {
	public DummyFrame() {
        this.setIconImage(Resource.getJarIcon(Resource.getValue(Resource.IMG_AppClientIcon)).getImage());
	}
}

