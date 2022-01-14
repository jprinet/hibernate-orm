/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.temporal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialect;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Gail Badner
 */
@RequiresDialect(value = MySQLDialect.class, majorVersion = 5, minorVersion = 7)
@TestForIssue(jiraKey = "HHH-8401")
@DomainModel(
		annotatedClasses = MySQL57TimestampPropertyTest.Entity.class
)
@SessionFactory
public class MySQL57TimestampPropertyTest {
	private final DateFormat timestampFormat = new SimpleDateFormat( "HH:mm:ss.SSS" );

	@Test
	public void testTime(SessionFactoryScope scope) {
		final Entity eOrig = new Entity();
		eOrig.ts = new Date();

		scope.inTransaction(
				session ->
						session.persist( eOrig )
		);

		scope.inTransaction(
				session -> {
					final Entity eGotten = session.get( Entity.class, eOrig.id );
					final String tsOrigFormatted = timestampFormat.format( eOrig.ts );
					final String tsGottenFormatted = timestampFormat.format( eGotten.ts );
					assertEquals( tsOrigFormatted, tsGottenFormatted );
				}
		);

		scope.inTransaction(
				session -> {
					final Query queryWithParameter = session.createQuery( "from Entity where ts= ?1" ).setParameter(
							1,
							eOrig.ts
					);
					final Entity eQueriedWithParameter = (Entity) queryWithParameter.uniqueResult();
					assertNotNull( eQueriedWithParameter );
				}
		);

		final Entity eQueriedWithTimestamp = scope.fromTransaction(
				session -> {
					final Query queryWithTimestamp = session.createQuery( "from Entity where ts= ?1" )
							.setParameter( 1, eOrig.ts, StandardBasicTypes.TIMESTAMP );
					final Entity queryResult = (Entity) queryWithTimestamp.uniqueResult();
					assertNotNull( queryResult );
					return queryResult;
				}
		);

		scope.inTransaction(
				session ->
						session.delete( eQueriedWithTimestamp )
		);
	}

	@Test
	public void testTimeGeneratedByColumnDefault(SessionFactoryScope scope) {
		final Entity eOrig = new Entity();

		scope.inTransaction(
				session ->
						session.persist( eOrig )
		);

		assertNotNull( eOrig.tsColumnDefault );

		scope.inTransaction(
				session -> {
					final Entity eGotten = session.get( Entity.class, eOrig.id );
					final String tsColumnDefaultOrigFormatted = timestampFormat.format( eOrig.tsColumnDefault );
					final String tsColumnDefaultGottenFormatted = timestampFormat.format( eGotten.tsColumnDefault );
					assertEquals( tsColumnDefaultOrigFormatted, tsColumnDefaultGottenFormatted );
				}
		);

		scope.inTransaction(
				session -> {
					final Query queryWithParameter =
							session.createQuery( "from Entity where tsColumnDefault= ?1" )
									.setParameter( 1, eOrig.tsColumnDefault );
					final Entity eQueriedWithParameter = (Entity) queryWithParameter.uniqueResult();
					assertNotNull( eQueriedWithParameter );
				}
		);

		final Entity eQueriedWithTimestamp = scope.fromTransaction(
				session -> {
					final Query queryWithTimestamp =
							session.createQuery( "from Entity where tsColumnDefault= ?1" )
									.setParameter( 1, eOrig.tsColumnDefault, StandardBasicTypes.TIMESTAMP );
					final Entity queryResult = (Entity) queryWithTimestamp.uniqueResult();
					assertNotNull( queryResult );
					return queryResult;
				}
		);

		scope.inTransaction(
				session ->
						session.delete( eQueriedWithTimestamp )
		);
	}

	@Test
	public void testTimeGeneratedByColumnDefinition(SessionFactoryScope scope) {
		final Entity eOrig = new Entity();

		scope.inTransaction(
				session ->
						session.persist( eOrig )
		);

		assertNotNull( eOrig.tsColumnDefinition );

		scope.inTransaction(
				session -> {
					final Entity eGotten = session.get( Entity.class, eOrig.id );
					final String tsColumnDefinitionOrigFormatted = timestampFormat.format( eOrig.tsColumnDefinition );
					final String tsColumnDefinitionGottenFormatted = timestampFormat.format( eGotten.tsColumnDefinition );
					assertEquals( tsColumnDefinitionOrigFormatted, tsColumnDefinitionGottenFormatted );
				}
		);

		scope.inTransaction(
				session -> {
					final Query queryWithParameter =
							session.createQuery( "from Entity where tsColumnDefinition= ?1" )
									.setParameter( 1, eOrig.tsColumnDefinition );
					final Entity eQueriedWithParameter = (Entity) queryWithParameter.uniqueResult();
					assertNotNull( eQueriedWithParameter );
				}
		);

		final Entity eQueriedWithTimestamp = scope.fromTransaction(
				session -> {
					final Query queryWithTimestamp =
							session.createQuery( "from Entity where tsColumnDefinition= ?1" )
									.setParameter( 1, eOrig.tsColumnDefinition, StandardBasicTypes.TIMESTAMP );
					final Entity queryResult = (Entity) queryWithTimestamp.uniqueResult();
					assertNotNull( queryResult );
					return queryResult;
				}
		);

		scope.inTransaction(
				session ->
						session.delete( eQueriedWithTimestamp )
		);
	}

	@jakarta.persistence.Entity(name = "Entity")
	public static class Entity {
		@GeneratedValue
		@Id
		private long id;

		@Temporal(value = TemporalType.TIMESTAMP)
		private Date ts;

		@Temporal(value = TemporalType.TIMESTAMP)
		@Generated(value = GenerationTime.INSERT)
		@ColumnDefault(value = "CURRENT_TIMESTAMP(6)")
		private Date tsColumnDefault;

		@Temporal(value = TemporalType.TIMESTAMP)
		@Generated(value = GenerationTime.INSERT)
		@Column(columnDefinition = "datetime(6) default NOW(6)")
		private Date tsColumnDefinition;

	}
}
