package pl.edu.utp.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import pl.edu.utp.backend.entity.Firma;
import pl.edu.utp.backend.entity.Kontakt;

import java.util.List;

public class ContactForm extends FormLayout {
    TextField imie = new TextField("Imię");
    TextField nazwisko = new TextField("Nazwisko");
    TextField email = new TextField("Email");
    ComboBox<Kontakt.Status> status = new ComboBox<>("Status");
    ComboBox<Firma> firma = new ComboBox<>("Firma");

    Button save = new Button("Zapisz");
    Button delete = new Button("Usuń");
    Button close = new Button("Anuluj");

    Binder<Kontakt> binder = new BeanValidationBinder<>(Kontakt.class);

    public ContactForm(List<Firma> firmy){
        addClassName("contact-form");
        binder.bindInstanceFields(this);
        firma.setItems(firmy);
        firma.setItemLabelGenerator(Firma::getNazwa);
        status.setItems(Kontakt.Status.values());
        add(imie,
                nazwisko,
                email,
                firma,
                status,
                createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout(){
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave(){
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setKontakt(Kontakt kontakt){
        binder.setBean(kontakt);
    }

    public static class ContactFormEvent extends ComponentEvent<ContactForm> {
        private Kontakt kontakt;

        protected ContactFormEvent(ContactForm source, Kontakt kontakt){
            super(source, false);
            this.kontakt = kontakt;
        }

        public Kontakt getKontakt(){
            return kontakt;
        }
    }

    public static class SaveEvent extends ContactFormEvent{
        SaveEvent(ContactForm source, Kontakt kontakt){
            super(source, kontakt);
        }
    }

    public static class DeleteEvent extends ContactFormEvent{
        DeleteEvent(ContactForm source, Kontakt kontakt){
            super(source, kontakt);
        }
    }

    public static class CloseEvent extends ContactFormEvent{
        CloseEvent(ContactForm source){
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener){
        return getEventBus().addListener(eventType, listener);
    }

}
