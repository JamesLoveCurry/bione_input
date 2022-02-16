package com.yusys.bione.comp.repository.jpa;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Implementation of BaseSearchProcessor that works with JPA.
 * 
 * <p>This class is designed to be used as a singleton. The constructor requires a
 * MetadataUtil instance. Each MetadataUtil instance is typically associated
 * with a single persistence unit (i.e. EntityManagerFactory). A
 * JPASearchProcessor can only be used with EntityManagers that are associated
 * with the same persistence unit as the MetadataUtil. If an application has
 * multiple persistence units, it will need to have multiple corresponding
 * Search Processors.
 * 
 * @author dwolverton
 */
public class JPASearchProcessor extends BaseSearchProcessor{
	
	private static Logger logger = LoggerFactory.getLogger(JPASearchProcessor.class);

	public JPASearchProcessor(JPAAnnotationMetadataUtil mdu) {
		super(QLTYPE_EQL, mdu);
	}

	// --- Public Methods ---

	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>Search</code> object.
	 * 
	 * @see Search
	 */
	public <X> List<X> search(EntityManager entityManager, Search search) {
		if (search == null)
			return null;

		return search(entityManager, search.getSearchClass(), search);
	}

	/**
	 * Search for objects based on the search parameters in the specified
	 * <code>Search</code> object. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see Search
	 */
	public <X> List<X> search(EntityManager entityManager, Class<?> searchClass, Search search) {
		if (searchClass == null || search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String ql = generateQL(searchClass, search, paramList);
		Query query = entityManager.createQuery(ql);
		addParams(query, paramList);
		addPaging(query, search);

		return transformResults(query.getResultList(), search);
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 * 
	 * @see Search
	 */
	public int count(EntityManager entityManager, Search search) {
		if (search == null)
			return 0;
		return count(entityManager, search.getSearchClass(), search);
	}

	/**
	 * Returns the total number of results that would be returned using the
	 * given <code>Search</code> if there were no paging or maxResult limits.
	 * Uses the specified searchClass, ignoring the searchClass specified on the
	 * search itself.
	 * 
	 * @see Search
	 */
	public int count(EntityManager entityManager, Class<?> searchClass, Search search) {
		if (searchClass == null || search == null)
			return 0;

		List<Object> paramList = new ArrayList<Object>();
		String ql = generateRowCountQL(searchClass, search, paramList);
		if (ql == null) { // special case where the query uses column operators
			return 1;
		}
		Query query = entityManager.createQuery(ql);
		addParams(query, paramList);

		return ((Number) query.getSingleResult()).intValue();
	}

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>.
	 * 
	 * @see Search
	 */
	/*
	public <X> SearchResult<X> searchAndCount(EntityManager entityManager, Search search) {
		if (search == null)
			return null;
		return searchAndCount(entityManager, search.getSearchClass(), search);
	}
	*/

	/**
	 * Returns a <code>SearchResult</code> object that includes the list of
	 * results like <code>search()</code> and the total length like
	 * <code>searchLength</code>. Uses the specified searchClass, ignoring the
	 * searchClass specified on the search itself.
	 * 
	 * @see Search
	 */
	/*
	@SuppressWarnings("unchecked")
	public <X> SearchResult<X> searchAndCount(EntityManager entityManager, Class<?> searchClass, Search search) {
		if (searchClass == null || search == null)
			return null;

		SearchResult<X> result = new SearchResult<X>();
		result.setResult((List<X>)search(entityManager, searchClass, search));

		if (search.getMaxResults() > 0) {
			result.setTotalCount(count(entityManager, searchClass, search));
		} else {
			result.setTotalCount(result.getResult().size() + SearchUtil.calcFirstResult(search));
		}

		return result;
	}
	*/

	/**
	 * Search for a single result using the given parameters.
	 */
	public <X> X searchUnique(EntityManager entityManager, Search search) throws NonUniqueResultException {
		if (search == null)
			return null;
		return searchUnique(entityManager, search.getSearchClass(), search);
	}

	/**
	 * Search for a single result using the given parameters. Uses the specified
	 * searchClass, ignoring the searchClass specified on the search itself.
	 */
	@SuppressWarnings("unchecked")
	public <X> X searchUnique(EntityManager entityManager, Class<?> entityClass, Search search)
			throws NonUniqueResultException {
		if (search == null)
			return null;

		List<Object> paramList = new ArrayList<Object>();
		String ql = generateQL(entityClass, search, paramList);
		Query query = entityManager.createQuery(ql);
		addParams(query, paramList);
		try {
			return (X)transformResult(query.getSingleResult(), search);
		} catch (NoResultException ex) {
			return (X)transformResult(null, search);
		}
	}

	// ---- SEARCH HELPERS ---- //

	private void addParams(Query query, List<Object> params) {
		StringBuilder debug = null;

		int i = 1;
		for (Object o : params) {
			if (logger.isDebugEnabled()) {
				if (debug == null)
					debug = new StringBuilder();
				else
					debug.append("\n\t");
				debug.append("p");
				debug.append(i);
				debug.append(": ");
				debug.append(InternalUtil.paramDisplayString(o));
			}
			query.setParameter("p" + Integer.toString(i++), o);
		}
		if (debug != null && debug.length() != 0) {
			logger.debug(debug.toString());
		}
	}

	private void addPaging(Query query, Search search) {
		int firstResult = SearchUtil.calcFirstResult(search);
		if (firstResult > 0) {
			query.setFirstResult(firstResult);
		}
		if (search.getMaxResults() > 0) {
			query.setMaxResults(search.getMaxResults());
		}
	}


	private Object transformResult(Object result, Search search) {
		List<Object> results = new ArrayList<Object>(1);
		results.add(result);
		return transformResults(results, search).get(0);
	}

	@SuppressWarnings("unchecked")
	private <X> List<X> transformResults(List<?> results, Search search) {
		if (results.size() == 0)
			return (List<X>)results;

		int resultMode = search.getResultMode();
		if (resultMode == Search.RESULT_AUTO) {
			int count = 0;
			Iterator<Field> fieldItr = search.getFields().iterator();
			while (fieldItr.hasNext()) {
				Field field = fieldItr.next();
				if (field.getKey() != null && !field.getKey().equals("")) {
					resultMode = Search.RESULT_MAP;
					break;
				}
				count++;
			}
			if (resultMode == Search.RESULT_AUTO) {
				if (count > 1)
					resultMode = Search.RESULT_ARRAY;
				else
					resultMode = Search.RESULT_SINGLE;
			}
		}

		switch (resultMode) {
		case Search.RESULT_ARRAY:
			if (!(results.get(0) instanceof Object[])) {
				List<Object[]> rArray = new ArrayList<Object[]>(results.size());
				for (Object result : results) {
					rArray.add(new Object[] { result });
				}
				return (List<X>)rArray;
			} else {
				return (List<X>)results;
			}
		case Search.RESULT_LIST:
			List<List<Object>> rList = new ArrayList<List<Object>>(results.size());
			if (results.get(0) instanceof Object[]) {
				for (Object[] result : (List<Object[]>) results) {
					List<Object> list = new ArrayList<Object>(result.length);
					for (Object o : result) {
						list.add(o);
					}
					rList.add(list);
				}
			} else {
				for (Object result : results) {
					List<Object> list = new ArrayList<Object>(1);
					list.add(result);
					rList.add(list);
				}
			}
			return (List<X>)rList;
		case Search.RESULT_MAP:
			List<String> keyList = new ArrayList<String>();
			Iterator<Field> fieldItr = search.getFields().iterator();
			while (fieldItr.hasNext()) {
				Field field = fieldItr.next();
				if (field.getKey() != null && !field.getKey().equals("")) {
					keyList.add(field.getKey());
				} else {
					keyList.add(field.getProperty());
				}
			}

			List<Map<String, Object>> rMap = new ArrayList<Map<String, Object>>(results.size());
			if (results.get(0) instanceof Object[]) {
				for (Object[] result : (List<Object[]>) results) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int i = 0; i < keyList.size(); i++) {
						String key = keyList.get(i);
						if (key != null) {
							map.put(key, result[i]);
						}
					}
					rMap.add(map);
				}
			} else if (keyList.size() == 1) {
				for (Object result : results) {
					Map<String, Object> map = new HashMap<String, Object>();
					;
					if (keyList.get(0) != null)
						map.put(keyList.get(0), result);
					rMap.add(map);
				}
			} else {
				throw new RuntimeException(
						"Unexpected condition: a single object was returned from the query for each record, but the Search expects multiple.");
			}

			return (List<X>)rMap;
		default: // Search.RESULT_SINGLE
			return (List<X>)results;
		}
	}
}