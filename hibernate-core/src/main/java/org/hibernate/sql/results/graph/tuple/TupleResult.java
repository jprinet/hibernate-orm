/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.tuple;

import org.hibernate.Internal;
import org.hibernate.query.spi.NavigablePath;
import org.hibernate.sql.results.graph.AssemblerCreationState;
import org.hibernate.sql.results.graph.DomainResult;
import org.hibernate.sql.results.graph.DomainResultAssembler;
import org.hibernate.sql.results.graph.FetchParentAccess;
import org.hibernate.sql.results.graph.basic.BasicResultGraphNode;
import org.hibernate.type.descriptor.java.JavaType;

/**
 * @author Christian Beikov
 */
public class TupleResult<T> implements DomainResult<T>, BasicResultGraphNode<T> {
	private final String resultVariable;
	private final JavaType<T> javaType;

	private final NavigablePath navigablePath;

	private final DomainResultAssembler<T> assembler;

	public TupleResult(
			int[] jdbcValuesArrayPositions,
			String resultVariable,
			JavaType<T> javaType) {
		this( jdbcValuesArrayPositions, resultVariable, javaType, (NavigablePath) null );
	}

	public TupleResult(
			int[] jdbcValuesArrayPositions,
			String resultVariable,
			JavaType<T> javaType,
			NavigablePath navigablePath) {
		this.resultVariable = resultVariable;
		this.javaType = javaType;

		this.navigablePath = navigablePath;

		this.assembler = new TupleResultAssembler<>( jdbcValuesArrayPositions, javaType );
	}

	@Override
	public String getResultVariable() {
		return resultVariable;
	}

	@Override
	public JavaType<T> getResultJavaType() {
		return javaType;
	}

	@Override
	public NavigablePath getNavigablePath() {
		return navigablePath;
	}

	/**
	 * For testing purposes only
	 */
	@Internal
	public DomainResultAssembler<T> getAssembler() {
		return assembler;
	}

	@Override
	public DomainResultAssembler<T> createResultAssembler(
			FetchParentAccess parentAccess,
			AssemblerCreationState creationState) {
		return assembler;
	}
}
