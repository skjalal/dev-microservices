package com.example.msscbeerservice.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Nullable;

@UtilityClass
public class BeanCopyUtils {

  public static void copyNonNullProperties(Object source, Object destination,
      @Nullable String... ignoreProperties) {
    Set<String> ignorePropertiesSet = getNullPropertyNames(source);
    Optional.ofNullable(ignoreProperties)
        .ifPresent(d -> Collections.addAll(ignorePropertiesSet, d));
    BeanUtils.copyProperties(source, destination, ignorePropertiesSet.toArray(new String[0]));
  }

  private static Set<String> getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
    Set<String> emptyNames = new HashSet<>();
    for (java.beans.PropertyDescriptor pd : pds) {
      //check if value of this property is null then add it to the collection
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null) {
        emptyNames.add(pd.getName());
      }
    }
    return emptyNames;
  }
}
