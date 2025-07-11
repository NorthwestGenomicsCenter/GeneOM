package org.uw.nwgc.multiomics.configuration;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;

public class CustomPostgresSQLDialect extends PostgreSQLDialect {

    /***
     * For our use case, we want to override the default column type for our
     * String properties to be a text column instead of a varchar(255) column
     * I understand that this may come with a performance hit, but the overall
     * quality of life improvement is worth that in this case.
     *
     * https://stackoverflow.com/questions/68966716/set-text-as-default-type-for-string-in-jpa
     */
    @Override
    protected void registerColumnTypes(
            TypeContributions typeContributions,
            ServiceRegistry serviceRegistry
    ) {
        super.registerColumnTypes(typeContributions, serviceRegistry);

        var ddlTypeRegistry = typeContributions
                .getTypeConfiguration()
                .getDdlTypeRegistry();

        ddlTypeRegistry.addDescriptor(
                new DdlTypeImpl(SqlTypes.VARCHAR, "text", this)
        );
    }
}