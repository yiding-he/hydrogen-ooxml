package com.hyd.ms.io.packaging;

import com.hyd.assertion.Assert;
import com.hyd.ms.io.IoException;
import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class PackUriHelper {

    public static final String ROOT_RELATION_URI = "/_rels/.rels";

    public static final URI PACKAGE_ROOT_URI = uri("/");

    public static final URI ROOT_RELATION_URI_OBJ = uri(ROOT_RELATION_URI);

    public static URI uri(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw new IoException(e);
        }
    }

    /**
     * returns a relationship part Uri given a part Uri
     * "/files/document.xaml" -> "/files/_rels/document.xaml.rels"
     * "/" -> "/_rels/.rels"
     */
    public static URI getRelationshipPartUri(URI partUri) {
        Assert.notNull(partUri, "partUri");
        if (partUri.toASCIIString().equals("/")) {
            return ROOT_RELATION_URI_OBJ;
        }

        Assert.not(isRelationshipPartUri(partUri), "no relationship for a relationship");
        return partUri.resolve("_rels/" + FilenameUtils.getName(partUri.toASCIIString()) + ".rels");
    }

    public static boolean isRelationshipPartUri(URI partUri) {
        return validatePartUri(partUri).isRelationshipPartUri();
    }

    public static boolean isRelationshipPartUri(ValidatedPartUri validatedPartUri) {
        return isRelationshipPartUri(validatedPartUri.getUri());
    }

    public static ValidatedPartUri validatePartUri(URI partUri) {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri is null");
        } else if (partUri.isAbsolute()) {
            throw new IllegalArgumentException("partUri is absolute");
        }

        String s = partUri.toASCIIString();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("partUri is empty");
        } else if (!s.startsWith("/")) {
            throw new IllegalArgumentException("partUri should starts with '/'");
        } else if (s.startsWith("//")) {
            throw new IllegalArgumentException("partUri should not starts with '//'");
        } else if (s.endsWith("/")) {
            throw new IllegalArgumentException("partUri should not ends with '/'");
        } else if (s.contains("#")) {
            throw new IllegalArgumentException("partUri should not contains '#'");
        }

        return new ValidatedPartUri(partUri);
    }

    ///////////////////////////////////////////////////////////////////

    /**
     * java.net.URI is not inheritable :(
     */
    public static class ValidatedPartUri implements Comparable<ValidatedPartUri> {

        private final URI uri;

        private final String normalized;

        public ValidatedPartUri(URI uri) {
            this.uri = uri;
            this.normalized = uri.toASCIIString().toLowerCase();
        }

        public URI getUri() {
            return uri;
        }

        @Override
        public int compareTo(ValidatedPartUri o) {
            return this.uri.compareTo(o.uri);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidatedPartUri that = (ValidatedPartUri) o;
            return uri.equals(that.uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri);
        }

        public boolean isRelationshipPartUri() {
            if (!normalized.endsWith(".rels")) {
                return false;
            }
            if (normalized.equals(ROOT_RELATION_URI)) {
                return true;
            }
            String[] segments = normalized.split("/");
            if (segments.length < 3) {
                return false;
            }

            return segments[segments.length - 2].equals("_rels") &&
                !segments[segments.length - 3].equals("_rels");
        }
    }
}
