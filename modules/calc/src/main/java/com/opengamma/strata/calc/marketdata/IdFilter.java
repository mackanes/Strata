/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.marketdata;

import java.lang.invoke.MethodHandles;

import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.data.MarketDataId;
import com.opengamma.strata.data.scenario.MarketDataBox;

/**
 * A market data filter that matches a specific identifier.
 * 
 * @param <T>  the type of the market data handled by the filter
 */
@BeanDefinition(style = "light", constructorScope = "package")
final class IdFilter<T>
    implements MarketDataFilter<T, MarketDataId<T>>, ImmutableBean {

  /**
   * The identifier that is matched by this filter.
   */
  @PropertyDefinition(validate = "notNull")
  private final MarketDataId<T> id;

  //-------------------------------------------------------------------------
  @Override
  public Class<?> getMarketDataIdType() {
    return id.getClass();
  }

  @Override
  public boolean matches(MarketDataId<T> marketDataId, MarketDataBox<T> marketData, ReferenceData refData) {
    return marketDataId.equals(id);
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code IdFilter}.
   */
  private static final MetaBean META_BEAN =
      LightMetaBean.of(
          IdFilter.class,
          MethodHandles.lookup(),
          new String[] {
              "id"},
          new Object[0]);

  /**
   * The meta-bean for {@code IdFilter}.
   * @return the meta-bean, not null
   */
  public static MetaBean meta() {
    return META_BEAN;
  }

  static {
    MetaBean.register(META_BEAN);
  }

  /**
   * Creates an instance.
   * @param id  the value of the property, not null
   */
  IdFilter(
      MarketDataId<T> id) {
    JodaBeanUtils.notNull(id, "id");
    this.id = id;
  }

  @Override
  @SuppressWarnings("unchecked")
  public TypedMetaBean<IdFilter<T>> metaBean() {
    return (TypedMetaBean<IdFilter<T>>) META_BEAN;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifier that is matched by this filter.
   * @return the value of the property, not null
   */
  public MarketDataId<T> getId() {
    return id;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      IdFilter<?> other = (IdFilter<?>) obj;
      return JodaBeanUtils.equal(id, other.id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(id);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("IdFilter{");
    buf.append("id").append('=').append(JodaBeanUtils.toString(id));
    buf.append('}');
    return buf.toString();
  }

  //-------------------------- AUTOGENERATED END --------------------------
}
