/***********************************************************************************
 * 
 * Copyright (c) 2015 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 *    
 * The Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    Olivier Matheret - add HTTP-Proxy feature
 *    
 */
package pl.baczkowicz.mqttspy.ui.controllers.edit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.baczkowicz.mqttspy.configuration.ConfiguredMqttConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.UserInterfaceMqttConnectionDetails;
import pl.baczkowicz.mqttspy.ui.controllers.EditMqttConnectionController;
import pl.baczkowicz.mqttspy.utils.ConnectionUtils;
import pl.baczkowicz.spy.common.generated.ProxyMode;
import pl.baczkowicz.spy.common.generated.ProxyModeEnum;
import pl.baczkowicz.spy.common.generated.ProxyModePassEnum;
import pl.baczkowicz.spy.configuration.BaseConfigurationUtils;
import pl.baczkowicz.spy.ui.keyboard.KeyboardUtils;
import pl.baczkowicz.spy.utils.ConversionUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for editing a single connection - security tab.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EditConnectionProxyController extends AnchorPane implements Initializable, IEditConnectionSubController
{
	/** The parent controller. */
	private EditMqttConnectionController parent;

	@FXML
	private TextField hostname;

	@FXML
	private TextField port;

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private ComboBox<ProxyModeEnum> modeCombo;

	@FXML
	private RadioButton askForPassword;

	@FXML
	private RadioButton predefinedPassword;

	@FXML
    private AnchorPane details;

	private final ChangeListener basicOnChangeListener = new ChangeListener()
	{
		@Override
		public void changed(ObservableValue observable, Object oldValue, Object newValue)
		{
			onChange();			
		}		
	};
	
	// ===============================
	// === Initialisation ============
	// ===============================

	public void initialize(URL location, ResourceBundle resources)
	{
		hostname.textProperty().addListener(basicOnChangeListener);
		port.textProperty().addListener(basicOnChangeListener);
        port.addEventFilter(KeyEvent.KEY_TYPED, KeyboardUtils.nonNumericKeyConsumer);
		username.textProperty().addListener(basicOnChangeListener);
		password.textProperty().addListener(basicOnChangeListener);

		modeCombo.getSelectionModel().selectedIndexProperty().addListener(basicOnChangeListener);

		predefinedPassword.selectedProperty().addListener(basicOnChangeListener);
		askForPassword.selectedProperty().addListener(basicOnChangeListener);
		predefinedPassword.selectedProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				password.setDisable(!(predefinedPassword.isSelected()));
			}
		});


		final Map<ProxyModeEnum, String> modeEnumText = new HashMap<>();
		modeEnumText.put(ProxyModeEnum.NO_PROXY, 			"No Proxy");
		modeEnumText.put(ProxyModeEnum.SOCKS, 				"SOCKS");
		modeEnumText.put(ProxyModeEnum.HTTP, 				"HTTP");

		modeCombo.setCellFactory(new Callback<ListView<ProxyModeEnum>, ListCell<ProxyModeEnum>>()
		{
			@Override
			public ListCell<ProxyModeEnum> call(ListView<ProxyModeEnum> l)
			{
				return new ListCell<ProxyModeEnum>()
				{
					@Override
					protected void updateItem(ProxyModeEnum item, boolean empty)
					{
						super.updateItem(item, empty);
						if (item == null || empty)
						{
							setText(null);
						}
						else
						{									
							setText(modeEnumText.get(item));
						}
					}
				};
			}
		});
		modeCombo.setConverter(new StringConverter<ProxyModeEnum>()
		{
			@Override
			public String toString(ProxyModeEnum item)
			{
				if (item == null)
				{
					return null;
				}
				else
				{
					return modeEnumText.get(item);
				}
			}

			@Override
			public ProxyModeEnum fromString(String id)
			{
				return null;
			}
		});
		
		for (ProxyModeEnum modeEnum : ProxyModeEnum.values())
		{
			modeCombo.getItems().add(modeEnum);
		}
	}

	public void init()
	{
	}

	// ===============================
	// === Logic =====================
	// ===============================

	public void onChange()
	{	
		parent.onChange();
	}
	

	@Override
	public UserInterfaceMqttConnectionDetails readValues(final UserInterfaceMqttConnectionDetails connection)
	{
		ProxyMode proxy = new ProxyMode();
		if (modeCombo.getSelectionModel().getSelectedItem() == null) {
			proxy.setType(ProxyModeEnum.NO_PROXY);
			details.setVisible(false);
		}
		else {
            details.setVisible(true);
			proxy.setType(modeCombo.getSelectionModel().getSelectedItem());
			proxy.setHostname(hostname.getText());
			if (!port.getText().isEmpty())
			    proxy.setPort(Integer.parseInt(port.getText()));
			proxy.setUsername(username.getText());
			if (askForPassword.isSelected()) {
				proxy.setPasswordType(ProxyModePassEnum.ASKEDFOR);
				proxy.setPassword(null);
			} else {
				proxy.setPasswordType(ProxyModePassEnum.PREDEFINED);
				proxy.setPassword(BaseConfigurationUtils.encodePassword(password.getText()));
			}
		}
		connection.setProxyMode(proxy);

		return connection;
	}

	
	@Override
	public void displayConnectionDetails(final ConfiguredMqttConnectionDetails connection)
	{
        // Connectivity
        ProxyModeEnum type = connection.getProxyMode().getType();
        if (type == null)
            type = ProxyModeEnum.NO_PROXY;
        modeCombo.getSelectionModel().select(type);

		hostname.setText(connection.getProxyMode().getHostname() != null ? connection.getProxyMode().getHostname() : "");

		port.setText(connection.getProxyMode().getPort() != null ? String.valueOf(connection.getProxyMode().getPort()) : "");
		username.setText(connection.getProxyMode().getUsername() != null ? connection.getProxyMode().getUsername() : "");
		if (connection.getProxyMode().getPasswordType() == null) { // old-fashioned
			predefinedPassword.setSelected(true);
			askForPassword.setSelected(false);
			password.setText(connection.getProxyMode().getPassword() != null ? connection.getProxyMode().getPassword() : "");
		}
		else if (connection.getProxyMode().getPasswordType() == ProxyModePassEnum.PREDEFINED) {
			predefinedPassword.setSelected(true);
			askForPassword.setSelected(false);
			password.setText(connection.getProxyMode().getPassword() != null ? BaseConfigurationUtils.decodePassword(connection.getProxyMode().getPassword()) : "");
			password.setDisable(false);
		}
		else {
			predefinedPassword.setSelected(false);
			askForPassword.setSelected(true);
			password.setText("");
			password.setDisable(true);
		}

        details.setVisible(type != ProxyModeEnum.NO_PROXY);
    }

	// ===============================
	// === Setters and getters =======
	// ===============================
	
	@Override
	public void setParent(final EditMqttConnectionController controller)
	{
		this.parent = controller;
	}
}
