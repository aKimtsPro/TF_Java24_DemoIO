package org.example.model;

import java.net.URL;

public record Task(
    long id,
    String title,
    String priority
) {}
