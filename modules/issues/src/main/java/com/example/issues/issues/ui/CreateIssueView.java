package com.example.issues.issues.ui;

import com.example.api.domain.Role;
import com.example.api.domain.User;
import com.example.api.service.AuthorizationService;
import com.example.api.ui.MainLayout;
import com.example.issues.issues.Issue;
import com.example.issues.issues.IssueService;
import com.example.issues.users.UserService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Set;

@Route(value = CreateIssueView.VIEW_NAME, layout = MainLayout.class)
@PageTitle("Create issue | Business Application")
public class CreateIssueView extends Composite<VerticalLayout> {

    public static final String VIEW_NAME = "create-issue";

    private TextField title = new TextField("Title");
    private TextArea description = new TextArea("Description");
    private ComboBox<User> owner = new ComboBox<>();

    private final IssueService issueService;
    private final AuthorizationService authorizationService;

    public CreateIssueView(IssueService issueService, UserService userService, AuthorizationService authorizationService) {
        this.issueService = issueService;
        this.authorizationService = authorizationService;

        Span viewTitle = new Span("Create issue");
        viewTitle.addClassName("view-title");

        title.setSizeFull();

        description.setSizeFull();

        Set<User> users = userService.findByRole(Role.DEVELOPER);
        owner.setItems(users);
        owner.setItemLabelGenerator(u -> u.getName());
        owner.setPlaceholder("Assign to...");

        Button create = new Button("Create", e -> create());
        create.getElement().setAttribute("theme", "primary");

        VerticalLayout formLayout = new VerticalLayout(
                viewTitle,
                title,
                description,
                authorizationService.secureComponent(owner, Role.ADMIN, Role.DEVELOPER),
                create
        );
        formLayout.setPadding(false);
        formLayout.setMargin(false);
        formLayout.setAlignSelf(FlexComponent.Alignment.END, owner, create);

        Div mainLayout = new Div(formLayout);
        mainLayout.setWidth("100%");
        getContent().add(mainLayout);
    }

    private void create() {
        try {
            Issue issue = new Issue();
            BeanValidationBinder<Issue> binder = new BeanValidationBinder<>(Issue.class);
            binder.bindInstanceFields(this);
            binder.writeBean(issue);
            issueService.create(issue);
            UI.getCurrent().navigate(IssueView.getViewName(issue.getId()));

        } catch (ValidationException e) {
            Notification.show("Please fix the errors and try again.");
        }
    }

}
