package com.sergio.bodegainfante.repositories;

import com.sergio.bodegainfante.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
