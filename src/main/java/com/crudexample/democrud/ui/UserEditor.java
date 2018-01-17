package com.crudexample.democrud.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.crudexample.democrud.model.Exp;
import com.crudexample.democrud.model.User;
import com.crudexample.democrud.repo.UserRepository;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout {
	private UserRepository repository;

	private User user;

	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField email = new TextField("Email");
	NativeSelect<Exp> experience = new NativeSelect<>("Experience");
	private DateField birthDate = new DateField("Birthday");
	
	Label label = new Label("Update User", ContentMode.HTML);
	
	Button saveButton = new Button("Save", VaadinIcons.CHECK);
	Button cancelButton = new Button("Cancel");
	Button deleteButton = new Button("Delete", VaadinIcons.TRASH);
	CssLayout actions = new CssLayout(saveButton, cancelButton, deleteButton);

	BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

	@Autowired
	public UserEditor(UserRepository repository) {
		HorizontalLayout hLayoutName = new HorizontalLayout(firstName, lastName);
		HorizontalLayout hLayout = new HorizontalLayout(birthDate, experience);
		VerticalLayout verticalLayout = new VerticalLayout(label,hLayoutName, hLayout, email, actions);
		verticalLayout.setWidth(800, Unit.PIXELS);
		
		// some styles
		birthDate.setIcon(VaadinIcons.DATE_INPUT);
		birthDate.setDateFormat("dd/MM/yyyy");

		experience.setIcon(VaadinIcons.EDIT);

		this.repository = repository;
		firstName.setPlaceholder("First Name");
		lastName.setPlaceholder("Last Name");
		firstName.setIcon(VaadinIcons.USER);

		email.setIcon(VaadinIcons.MAILBOX);
		experience.setItems(Exp.values());
		experience.setWidth(185, Unit.PIXELS);

		lastName.setIcon(VaadinIcons.USER_CHECK);
		lastName.setDescription("Last name cannot be empty");
		lastName.setComponentError(new UserError("Last name cannot be empty"));

		addComponents(verticalLayout);
		binder.bindInstanceFields(this);


		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);

		// Click events
		saveButton.addClickListener(e -> repository.save(user));
		deleteButton.addClickListener(e -> repository.delete(user));
		cancelButton.addClickListener(e -> {
			editUser(user);
			setVisible(false);
		});
		setVisible(false);
	}

	public final void editUser(User users) {
		if (users == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = users.getId() != null;
		if (persisted) {
			user = repository.findOne(users.getId());
			label.setValue("<b>Update User</b>");
		} else {
			user = users;
			label.setValue("<b>Add New User</b>");
		}
		deleteButton.setVisible(persisted);
		lastName.setEnabled(!persisted);

		// Data binding
		binder.setBean(user);
		setVisible(true);

		saveButton.focus();
		firstName.selectAll();

	}

	public interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler(ChangeHandler handler) {
		saveButton.addClickListener(e -> handler.onChange());
		deleteButton.addClickListener(e -> handler.onChange());
	}
}
