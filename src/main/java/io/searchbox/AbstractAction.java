package io.searchbox;

import io.searchbox.annotations.JestId;
import io.searchbox.core.Doc;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dogukan Sonmez
 */


public abstract class AbstractAction implements Action {

	final static Logger log = LoggerFactory.getLogger(AbstractAction.class);

	private Object data;

	private String URI;

	private String restMethodName;

	private boolean isBulkOperation = false;

	public String getIndexName() {
		return indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getId() {
		return id;
	}

	protected String indexName;

	protected String typeName;

	protected String id;

	private String pathToResult;

	private final ConcurrentMap<String, Object> parameterMap = new ConcurrentHashMap<String, Object>();

	public void setRestMethodName(String restMethodName) {
		this.restMethodName = restMethodName;
	}

	public void addParameter(String parameter, Object value) {
		parameterMap.put(parameter, value);
	}

	public void removeParameter(String parameter) {
		parameterMap.remove(parameter);
	}

	public boolean isParameterExist(String parameter) {
		return parameterMap.containsKey(parameter);
	}

	public Object getParameter(String parameter) {
		return parameterMap.get(parameter);
	}

	public void setURI(String URI) {
		this.URI = URI;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getURI() {
		if (parameterMap.size() > 0) {
			URI = URI + buildQueryString();
		}
		return URI;
	}

	public String getRestMethodName() {
		return restMethodName;
	}

	public Object getData() {
		return data;
	}

	public String getName() {
		return null;
	}

	public String getPathToResult() {
		return pathToResult;
	}

	public String getIdFromSource(Object source) {
		if (source == null) return null;
		Field[] fields = source.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(JestId.class)) {
				try {
					field.setAccessible(true);
					Object name = field.get(source);
					return name == null ? null : name.toString();
				} catch (IllegalAccessException e) {
					log.error("Unhandled exception occurred while getting annotated id from source");
				}
			}
		}
		return null;
	}

	protected String buildURI(Doc doc) {
		return buildURI(doc.getIndex(), doc.getType(), doc.getId());
	}

	protected String buildURI(String index, String type, String id) {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isNotBlank(index)) {
			sb.append(index);
		}

		if (StringUtils.isNotBlank(type)) {
			sb.append("/").append(type);
		}

		if (StringUtils.isNotBlank(id)) sb.append("/").append(id);

		log.debug("Created uri: " + sb.toString());

		return sb.toString();
	}
	protected String buildQueryString() {
		StringBuilder queryString = new StringBuilder("");
		for (Map.Entry<?, ?> entry : parameterMap.entrySet()) {
			if (queryString.length() == 0) {
				queryString.append("?");
			} else {
				queryString.append("&");
			}
			queryString.append(entry.getKey().toString())
					.append("=")
					.append(entry.getValue().toString());
		}
		return queryString.toString();
	}

	protected boolean isValid(String index, String type, String id) {
		return StringUtils.isNotBlank(index) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(id);
	}

	protected boolean isValid(Doc doc) {
		return isValid(doc.getIndex(), doc.getType(), doc.getId());
	}

	public boolean isBulkOperation() {
		return isBulkOperation;
	}

	public void setBulkOperation(boolean bulkOperation) {
		isBulkOperation = bulkOperation;
	}

	public void setPathToResult(String pathToResult) {
		this.pathToResult = pathToResult;
	}
}
