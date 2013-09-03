package cz.cuni.mff.d3s.been.web.model;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used as simple storage for session conversations. Usage in web pages as following:
 * <pre>
 *
 *     ...
 *     class Page {
 *         {@literal @}SessionState
 *         private {@link ConversationHolder} holder;
 *
 *         private MyObject obj;
 *
 *         private String conversationId;
 *
 *         Object onActivate() {
 *              if (conversationId == null) {
 *                  mo = new MyObject();
 *                  conversationId = holder.set(mo);
 *                  return this;
 *              }
 *              return null;
 *         }
 *
 *         Object onActivate(String conversationId) {
 *             if (!holder.contains(conversationId)) {
 *                 // conversation does not exists - inform user in proper way
 *             } else {
 *                 this.conversationId = conversationId;
 *                 this.obj = (MyObject) holder.get(conversationId);
 *             }
 *
 *             return null;
 *         }
 *
 *         object onPassivate() {
 *              return conversationId;
 *         }
 *
 *         ...
 *
 *     }
 *
 * </pre>
 */
public class ConversationHolder<Type> {

    /**
     * Used as id in holder map for next inserted object. Counter is
     * increased each time the new object is inserted.
     */
    private int counter = 0;

    /**
     * Synchronized storage for conoversation objects.
     */
    private Map<String, Type> holder = Collections.synchronizedMap(new HashMap<String, Type>());


    /**
     * Retrieves object stored under the given id
	 *
     * @param id ID of the object to retrieve
	 *
     * @return retrieved object or null if object was not found
     */
    public Type get(String id) {
        return holder.get(id);
    }

    /**
     * Adds object to session holder. Object will be accessible under the returned identifier
	 *
     * @param object Object to add
	 *
     * @return identifier of created object
     */
    public String set(Type object) {
        holder.put(String.valueOf(counter), object);
        return String.valueOf(counter++);
    }

    /**
     * Removes object with given id from session holder.
	 *
     * @param id ID of the object to remove
     */
    public void remove(String id) {
        holder.remove(id);
    }

    /**
     * Checks if holder contains object under the given id
	 *
     * @param id ID of the object to check
	 *
     * @return <code>true</code> if the holder contains the object, <code>false</code> otherwise
     */
    public boolean contains(String id)  {
        return holder.containsKey(id);
    }

}
