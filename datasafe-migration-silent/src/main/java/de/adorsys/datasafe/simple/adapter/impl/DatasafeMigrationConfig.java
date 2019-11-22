package de.adorsys.datasafe.simple.adapter.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.javacrumbs.shedlock.core.LockProvider;

@AllArgsConstructor
@Getter
public class DatasafeMigrationConfig {
    private final LockProvider lockProvider;
    private final boolean distinctFolder;

}
