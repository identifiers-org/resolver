package org.identifiers.sparql

import groovy.sql.Sql
import uk.ac.ebi.miriam.common.URIextended

import info.aduna.iteration.CloseableIteration
import info.aduna.iteration.EmptyIteration

import javax.sql.DataSource
import java.util.ArrayList
import java.util.Iterator
import java.util.List
import org.openrdf.model.Resource
import org.openrdf.model.URI
import org.openrdf.model.Value
import org.openrdf.model.ValueFactory
import org.openrdf.model.impl.StatementImpl
import org.openrdf.model.vocabulary.OWL
import org.openrdf.query.QueryEvaluationException
import org.openrdf.query.algebra.evaluation.TripleSource


class TripleSource implements org.openrdf.query.algebra.evaluation.TripleSource
{
//    private static final String SAME_AS_QUERY = """SELECT convertPrefix, obsolete, (SELECT convertPrefix FROM mir_resource WHERE convertPrefix LIKE ? LIMIT 1 ORDER BY size(convertPrefix)) AS original_prefix FROM mir_resource WHERE urischeme =1 AND ptr_datatype = (SELECT ptr_datatype FROM mir_resource WHERE convertPrefix LIKE ?)"""
    private static final String CONVERT_PREFIX = "SELECT convertPrefix FROM mir_resource " +
            "WHERE convertPrefix LIKE ? " +
            "UNION SELECT convertPrefix FROM mir_uri " +
            "WHERE convertPrefix LIKE  ? " +
            "LIMIT 1";

    private static final String SAME_AS_QUERY_CONPREFIX = "SELECT convertPrefix, obsolete AS deprecated, " +
            "(SELECT convertPrefix FROM mir_resource WHERE convertPrefix LIKE ? UNION SELECT convertPrefix FROM mir_uri WHERE convertPrefix LIKE ? LIMIT 1 ) AS original_prefix " +
            "FROM mir_resource WHERE ptr_datatype = ( " +
            "SELECT ptr_datatype FROM mir_resource WHERE convertPrefix LIKE ? UNION SELECT ptr_datatype FROM mir_uri WHERE convertPrefix LIKE ? LIMIT 1 ) " +
            "UNION " +
            "SELECT convertPrefix, deprecated AS deprecated, " +
            "(SELECT convertPrefix FROM mir_resource WHERE convertPrefix LIKE ? UNION SELECT convertPrefix FROM mir_uri WHERE convertPrefix LIKE ? LIMIT 1 ) AS original_prefix " +
            "FROM mir_uri WHERE ptr_datatype = ( " +
            "SELECT ptr_datatype FROM mir_resource WHERE convertPrefix LIKE  ? UNION SELECT ptr_datatype FROM mir_uri WHERE convertPrefix LIKE ? LIMIT 1 )";

    private static final String SAME_AS_QUERY_URLPREFIX = "SELECT url_element_prefix AS convertPrefix, obsolete AS deprecated, " +
            "(SELECT url_element_prefix AS convertPrefix FROM mir_resource WHERE url_element_prefix LIKE ? UNION SELECT uri AS convertPrefix FROM mir_uri WHERE uri LIKE ? LIMIT 1 ) AS original_prefix " +
            "FROM mir_resource WHERE ptr_datatype = ( " +
            "SELECT ptr_datatype FROM mir_resource WHERE url_element_prefix LIKE ? UNION SELECT ptr_datatype FROM mir_uri WHERE uri LIKE ? LIMIT 1 ) " +
            "UNION " +
            "SELECT uri AS convertPrefix, deprecated AS deprecated, " +
            "(SELECT url_element_prefix AS convertPrefix FROM mir_resource WHERE url_element_prefix LIKE ? UNION SELECT uri AS convertPrefix FROM mir_uri WHERE uri LIKE ? LIMIT 1 ) AS original_prefix " +
            "FROM mir_uri WHERE ptr_datatype = ( " +
            "SELECT ptr_datatype FROM mir_resource WHERE url_element_prefix LIKE ? UNION SELECT ptr_datatype FROM mir_uri WHERE uri LIKE ? LIMIT 1) ";

    private ValueFactory vf;   // final
    private DataSource dataSource;

    public TripleSource(ValueFactory value, DataSource dataSource)
    {
        vf = value;
        this.dataSource = dataSource;
    }

    @Override
    public CloseableIteration<StatementImpl, QueryEvaluationException> getStatements(
            Resource subj, URI pred, Value obj, Resource... contexts)
            throws QueryEvaluationException {
        if (!pred.equals(OWL.SAMEAS))
            return new EmptyIteration<StatementImpl, QueryEvaluationException>();
        if (subj == null && obj == null)
            return new EmptyIteration<StatementImpl, QueryEvaluationException>();
        final Iterator<StatementImpl> iter = getIterViaJDBC(subj, obj, contexts);
        return new CloseableIteration<StatementImpl, QueryEvaluationException>() {

            @Override
            public boolean hasNext() throws QueryEvaluationException {
                return iter.hasNext();
            }

            @Override
            public StatementImpl next() throws QueryEvaluationException {
                return iter.next();
            }

            @Override
            public void remove() throws QueryEvaluationException {
                iter.remove();

            }

            @Override
            public void close() throws QueryEvaluationException {
                // TODO Auto-generated method stub
            }
        };
    }

    private Iterator<StatementImpl> getIterViaJDBC(Resource subj, Value obj, Resource... contexts)
    {
        if (obj instanceof Resource) {
            return getResultsViaJDBC(subj, (Resource) obj, contexts);
        } else
            return getResultsViaJDBC(subj, contexts);
    }

    private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj, Resource... contexts)
    {
        final Iterator<URIextended> iter = getSameAsURIs(subj.stringValue()).iterator();

        return new Iterator<StatementImpl>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public StatementImpl next() {
                final URIextended next = iter.next();
                URI uri = vf.createURI(next.getURI());
                return new StatementImpl(subj, OWL.SAMEAS, uri);
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj,
                                                      final Resource obj, Resource... contexts) {

        if (subj == null)
        {
            final Iterator<URIextended> iter = getSameAsURIs(obj.stringValue()).iterator();
            return new Iterator<StatementImpl>(){
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public StatementImpl next() {
                    final URIextended next = iter.next();
                    URI uri = vf.createURI(next.getURI());
                    return new StatementImpl(subj, OWL.SAMEAS, uri);
                }

                @Override
                public void remove() {
                    iter.remove();
                }
            };
        }
        else
        {
            final Iterator<URIextended> iter = getSameAsURIs(obj.stringValue()).iterator();
            List<StatementImpl> l = new ArrayList<>(1);
            while (iter.hasNext()) {
                final URIextended next = iter.next();
                if (subj.stringValue().equals(next.getURI())) {
                    l.add(new StatementImpl(subj, OWL.SAMEAS, obj));
                }
            }
            return l.iterator();
        }
    }

    @Override
    public ValueFactory getValueFactory() {
        return vf;
    }

    /**
     * Returns all URIs sameAs the provided one.
     *
     * @param uri
     * @param dataSource database connection
     * @return
     */
    public List<URIextended> getSameAsURIs(String uri)
    {
        List<URIextended> urls = new ArrayList<URIextended>();
        final String uriTobe = uri.substring(0, uri.lastIndexOf("/") + 1) + '%';

        Sql sql = new Sql(dataSource)
        String query;
        String urn_separater= "";

        def params = [uriTobe,uriTobe]
        //check whether uriTobe exists in convert prefix
        if(sql.rows(CONVERT_PREFIX,params).size()!=0){
            query = SAME_AS_QUERY_CONPREFIX;
        }else{
            query = SAME_AS_QUERY_URLPREFIX;
            urn_separater = ":";
        }
        params = [uriTobe, uriTobe, uriTobe, uriTobe, uriTobe, uriTobe, uriTobe, uriTobe]
        sql.eachRow(query, params) {
            String identifier = uri.substring(it.original_prefix.size())
            String uri2 = it.convertPrefix;
            if(!uri2.startsWith("http"))
                uri2 = uri2 + urn_separater + identifier
            else
                uri2 = uri2 + identifier
            urls.add(new URIextended(uri2, it.deprecated))
        }

        return urls;
    }
}
