/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.profile;

import org.hibernate.metamodel.RuntimeMetamodels;
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.sql.results.graph.FetchOptions;
import org.hibernate.tuple.NonIdentifierAttribute;

import java.util.Map;

import static org.hibernate.engine.FetchStyle.SUBSELECT;
import static org.hibernate.engine.FetchTiming.IMMEDIATE;
import static org.hibernate.engine.FetchStyle.JOIN;

/**
 * @author Gavin King
 */
public class DefaultFetchProfile extends FetchProfile {
	/**
	 * The name of an implicit fetch profile which includes all eager to-one associations.
	 */
	public static final String HIBERNATE_DEFAULT_PROFILE = "org.hibernate.defaultProfile";
	private final RuntimeMetamodels metamodels;

	public DefaultFetchProfile(RuntimeMetamodels metamodels) {
		super(HIBERNATE_DEFAULT_PROFILE);
		this.metamodels = metamodels;
	}

	@Override
	public Fetch getFetchByRole(String role) {
		final int last = role.lastIndexOf('.');
		final String entityName = role.substring( 0, last );
		final String property = role.substring( last + 1 );
		final EntityMappingType entity = metamodels.getEntityMappingType( entityName );
		if ( entity != null ) {
			final AttributeMapping attributeMapping = entity.findAttributeMapping( property );
			if ( attributeMapping != null && !attributeMapping.isPluralAttributeMapping() ) {
				final FetchOptions fetchOptions = attributeMapping.getMappedFetchOptions();
				if ( fetchOptions.getStyle() == JOIN && fetchOptions.getTiming() == IMMEDIATE ) {
					return new Fetch( new Association( entity.getEntityPersister(), role ), JOIN, IMMEDIATE );
				}
			}
		}
		return super.getFetchByRole( role );
	}

	@Override
	public boolean hasSubselectLoadableCollectionsEnabled(EntityPersister persister) {
		final EntityMappingType entity = metamodels.getEntityMappingType( persister.getEntityName() );
		for ( AttributeMapping attributeMapping : entity.getAttributeMappings() ) {
			if ( attributeMapping.getMappedFetchOptions().getStyle() == SUBSELECT ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Fetch> getFetches() {
		throw new UnsupportedOperationException( "DefaultFetchProfile has implicit fetches" );
	}
}
