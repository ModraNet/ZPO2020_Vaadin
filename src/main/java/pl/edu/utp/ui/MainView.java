package pl.edu.utp.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import pl.edu.utp.backend.entity.Kontakt;
import pl.edu.utp.backend.service.FirmaService;
import pl.edu.utp.backend.service.KontaktService;

@Route("")
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout{

    private KontaktService kontaktService;
    private Grid<Kontakt> grid = new Grid<>(Kontakt.class);
    private TextField filterText = new TextField();
    private ContactForm form;

    public MainView(KontaktService kontaktService, FirmaService firmaService) {
        this.kontaktService = kontaktService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new ContactForm(firmaService.findAll());
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void saveContact(ContactForm.SaveEvent event){
        kontaktService.save(event.getKontakt());
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent event){
        kontaktService.delete(event.getKontakt());
        updateList();
        closeEditor();
    }

    private void updateList(){
        grid.setItems(kontaktService.findAll(filterText.getValue()));
    }

    private HorizontalLayout getToolbar(){
        filterText.setPlaceholder("Filtruj po nazwie...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Dodaj nowy kontakt");
        addContactButton.addClickListener(click -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    void addContact(){
        grid.asSingleSelect().clear();
        editContact(new Kontakt());
    }

    public void editContact(Kontakt kontakt){
        if(kontakt == null){
            closeEditor();
        } else {
            form.setKontakt(kontakt);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor(){
        form.setKontakt(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void configureGrid(){
        grid.addClassName("kontakt-grid");
        grid.setSizeFull();
        grid.setColumns("imie", "nazwisko", "email", "status");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
    }
}
