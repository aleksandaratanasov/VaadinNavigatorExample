package com.example.vaadinnavigatorexample;

import java.io.File;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
@Theme("vaadinnavigatorexample")
public class VaadinnavigatorexampleUI extends UI {
	
	Navigator navigator;
	protected static final String MAINVIEW = "main";

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VaadinnavigatorexampleUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Navigation Example");
		
		// create a navigator to control the views
		navigator = new Navigator(this, this);
		
		// create and register the views
		navigator.addView("", new StartView());
		navigator.addView(MAINVIEW, new MainView());
	}
	
	
	public class StartView extends VerticalLayout implements View {

		public StartView() {
			Button button = new Button("Welcome! Please click here to continue", new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					navigator.navigateTo(MAINVIEW);
				}
			});
			button.setStyleName(BaseTheme.BUTTON_LINK);
			
			addComponent(button);
			setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
		
		/* (non-Javadoc)
		 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
		 */
		@Override
		public void enter(ViewChangeEvent event) {
			com.vaadin.ui.Notification.show("This is a demonstration of Vaadin Navigator");
		}

	}

	/** Main view with a menu (with declarative layout design */
	@DesignRoot
	public class MainView extends VerticalLayout implements View {
		// Menu navigation button listener
		class ButtonListener implements ClickListener {
			String menuitem;
			public ButtonListener(String menuitem) {
				this.menuitem = menuitem;
			}
			
			@Override
			public void buttonClick(ClickEvent event) {
				// Navigate to a specific state
				navigator.navigateTo(MAINVIEW + "/" + menuitem);
			}
		}
		
		VerticalLayout menuContent;
		Panel mainPanel;
		Button logout;
		
		public MainView() {
			Design.read(this);
			
			logout.setStyleName(BaseTheme.BUTTON_LINK);
			
			Button buttonOwnDashboard = new Button("Return to your dashboard");
			buttonOwnDashboard.addClickListener(new ButtonListener("own dashboard"));
			buttonOwnDashboard.setStyleName(BaseTheme.BUTTON_LINK);
			menuContent.addComponent(buttonOwnDashboard);
			
			Button buttonProfileSettings = new Button("Profile settings");
			buttonProfileSettings.addClickListener(new ButtonListener("profile settings"));
			buttonProfileSettings.setStyleName(BaseTheme.BUTTON_LINK);
			menuContent.addComponent(buttonProfileSettings);
			
			// Allow going back to the start
			logout.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					navigator.navigateTo("");
				}
			});
		}
		
		@DesignRoot
		class MainPanelView extends VerticalLayout {
			Label watching;
			Embedded pic;
			
			public MainPanelView(String notification) {
				Design.read(this);
				
				// Find the application directory
				//String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath(); // NOT FUCKIN WORKIN, GOD DAMN IT!!!
				
				watching.setValue("You are currently watching your " + notification);
				pic.setSource(new FileResource(new File("/home/redbaron/workspace/VaadinNavigatorExample/WebContent/WEB-INF/images/awesomeness.jpg")));
			}
		}
		
		@Override
		public void enter(ViewChangeEvent event) {
			if(event.getParameters() == null || event.getParameters().isEmpty()) {
				mainPanel.setContent(new Label("Currently nothing has been selected. Please choose one of the options in the control panel on the left"));
				return;
			}
			else mainPanel.setContent( new MainPanelView(event.getParameters()));
		}
	}
}