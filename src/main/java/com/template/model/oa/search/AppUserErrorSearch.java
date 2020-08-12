// Generated by OABuilder
package com.template.model.oa.search;

import java.util.logging.*;
import com.template.model.oa.*;
import com.template.model.oa.propertypath.*;
import com.viaoa.annotation.*;
import com.viaoa.datasource.*;
import com.viaoa.object.*;
import com.viaoa.hub.*;
import com.viaoa.util.*;
import com.viaoa.filter.OAQueryFilter;

@OAClass(useDataSource=false, localOnly=true)
public class AppUserErrorSearch extends OAObject {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(AppUserErrorSearch.class.getName());
    public static final String P_MaxResults = "MaxResults";

    protected int maxResults;

    public int getMaxResults() {
        return maxResults;
    }
    public void setMaxResults(int newValue) {
        fireBeforePropertyChange(P_MaxResults, this.maxResults, newValue);
        int old = maxResults;
        this.maxResults = newValue;
        firePropertyChange(P_MaxResults, old, this.maxResults);
    }

    public void reset() {
    }

    public boolean isDataEntered() {
        return false;
    }

    protected String extraWhere;
    protected Object[] extraWhereParams;
    protected OAFilter<AppUserError> filterExtraWhere;

    public void setExtraWhere(String s, Object ... args) {
        this.extraWhere = s;
        this.extraWhereParams = args;
        if (!OAString.isEmpty(s) && getExtraWhereFilter() == null) {
            OAFilter<AppUserError> f = new OAQueryFilter<AppUserError>(AppUserError.class, s, args);
            setExtraWhereFilter(f);
        }
    }
    public void setExtraWhereFilter(OAFilter<AppUserError> filter) {
        this.filterExtraWhere = filter;
    }
    public OAFilter<AppUserError> getExtraWhereFilter() {
        return this.filterExtraWhere;
    }

    public OASelect<AppUserError> getSelect() {
        String sql = "";
        String sortOrder = null;
        Object[] args = new Object[0];

        if (!OAString.isEmpty(extraWhere)) {
            if (sql.length() > 0) sql = "(" + sql + ") AND ";
            sql += extraWhere;
            args = OAArray.add(Object.class, args, extraWhereParams);
        }

        OASelect<AppUserError> select = new OASelect<AppUserError>(AppUserError.class, sql, args, sortOrder);
        select.setDataSourceFilter(this.getDataSourceFilter());
        select.setFilter(this.getCustomFilter());
        if (getMaxResults() > 0) select.setMax(getMaxResults());
        return select;
    }

    public void appendSelect(final String fromName, final OASelect select) {
        final String prefix = fromName + ".";
        String sql = "";
        Object[] args = new Object[0];
        select.add(sql, args);
    }

    private OAFilter<AppUserError> filterDataSourceFilter;
    public OAFilter<AppUserError> getDataSourceFilter() {
        if (filterDataSourceFilter != null) return filterDataSourceFilter;
        filterDataSourceFilter = new OAFilter<AppUserError>() {
            @Override
            public boolean isUsed(AppUserError appUserError) {
                return AppUserErrorSearch.this.isUsedForDataSourceFilter(appUserError);
            }
        };
        return filterDataSourceFilter;
    }
    
    private OAFilter<AppUserError> filterCustomFilter;
    public OAFilter<AppUserError> getCustomFilter() {
        if (filterCustomFilter != null) return filterCustomFilter;
        filterCustomFilter = new OAFilter<AppUserError>() {
            @Override
            public boolean isUsed(AppUserError appUserError) {
                boolean b = AppUserErrorSearch.this.isUsedForCustomFilter(appUserError);
                if (b && filterExtraWhere != null) b = filterExtraWhere.isUsed(appUserError);
                return b;
            }
        };
        return filterCustomFilter;
    }
    
    public boolean isUsedForDataSourceFilter(AppUserError searchAppUserError) {
        return true;
    }
    public boolean isUsedForCustomFilter(AppUserError searchAppUserError) {
        return true;
    }
}
