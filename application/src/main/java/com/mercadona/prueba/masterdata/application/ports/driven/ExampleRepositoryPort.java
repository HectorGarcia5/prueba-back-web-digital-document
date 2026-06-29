package com.mercadona.prueba.masterdata.application.ports.driven;

import com.mercadona.framework.cna.commons.interfaces.CNACrudRepository;
import com.mercadona.prueba.masterdata.domain.Example;

public interface ExampleRepositoryPort extends CNACrudRepository<Example, Long> { }
