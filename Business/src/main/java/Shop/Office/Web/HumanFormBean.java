package Shop.Office.Web;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import Shop.Office.Persons.Human;
import Shop.Office.Administration.AdminManager;

@SessionScoped
@Named("HumanBean")
public class HumanFormBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Human person = new Human();

	private ListDataModel<Human> persons = new ListDataModel<Human>();

	@Inject
	private AdminManager pm;

	public Human getPerson() {
		return person;
	}

	public void setPerson(Human person) {
		this.person = person;
	}

	public ListDataModel<Human> getAllPersons() {
		persons.setWrappedData(pm.getAllPersons());
		return persons;
	}

	// Actions
	public String addPerson() {
		pm.addPerson(person);
		return "showPersons";
		//return null;
	}

	public String deletePerson() {
		Human personToDelete = persons.getRowData();
		pm.deletePerson(personToDelete);
		return null;
	}

	// Validators

	// Business logic validation
	public void uniquePin(FacesContext context, UIComponent component,
			Object value) {

		String pesel = (String) value;

		for (Human person : pm.getAllPersons()) {
			if (person.getPesel().equalsIgnoreCase(pesel)) {
				FacesMessage message = new FacesMessage(
						"Osoba z tym nr. Pesel już istnieje w bazie danych");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}
		}
	}

	// Multi field validation with <f:event>
	// Rule: first two digits of PIN must match last two digits of the year of
	// birth
	public void validatePinDob(ComponentSystemEvent event) {

		UIForm form = (UIForm) event.getComponent();
		UIInput pin = (UIInput) form.findComponent("pin");
		UIInput dob = (UIInput) form.findComponent("dob");

		if (pin.getValue() != null && dob.getValue() != null
				&& pin.getValue().toString().length() >= 2) {
			String twoDigitsOfPin = pin.getValue().toString().substring(0, 2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(((Date) dob.getValue()));

			String lastDigitsOfDob = ((Integer) cal.get(Calendar.YEAR))
					.toString().substring(2);

			if (!twoDigitsOfPin.equals(lastDigitsOfDob)) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(form.getClientId(), new FacesMessage(
						"PIN doesn't match date of birth"));
				context.renderResponse();
			}
		}
	}
}
