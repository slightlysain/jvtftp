package slightlysain.mock;

import org.hamcrest.Description;

import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class CatchInstanceAction<T> implements Action {
    private T instance;

    public T getInstance() {
        return instance;
    }

	public void describeTo(Description description) {
	     description.appendText("catching packet handler");
	}

	public Object invoke(Invocation i) throws Throwable {
		instance = (T) i.getParameter(0);
		return null;
	}
}

//public class AddElementsAction<T> implements Action {
//    private Collection<T> elements;
//    
//    public AddElementsAction(Collection<T> elements) {
//        this.element = elements;
//    }
//    
//    public Object invoke(Invocation invocation) throws Throwable {
//        ((Collection<T>)invocation.parameterValues.get(0)).addAll(elements);
//        return null;
//    }
//}