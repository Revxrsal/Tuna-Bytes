package io.tunabytes.ap;

import io.tunabytes.Mixin;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * An annotation processor for generating mixins.properties and
 * mixins-neighbors.properties.
 */
@SupportedAnnotationTypes("io.tunabytes.Mixin")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MixinsProcessor extends AbstractProcessor {

    private final StringJoiner mixins = new StringJoiner(System.lineSeparator());
    private final Map<String, String> neighbors = new HashMap<>();

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Mixin.class)) {
            TypeElement type = (TypeElement) element;
            Mixin mixin = type.getAnnotation(Mixin.class);
            String packageName = "";
            try {
                if (!mixin.name().isEmpty()) {
                    packageName = mixin.name().substring(0, mixin.name().lastIndexOf('.'));
                    mixins.add(type.getQualifiedName().toString() + "=" + mixin.name());
                } else {
                    mixin.value();
                }
            } catch (MirroredTypeException e) {
                if (mixin.name().isEmpty() && e.getTypeMirror().toString().equals("java.lang.Object")) {
                    throw new IllegalStateException("@Mixin on " + type.getQualifiedName() + " does not specify a class name() or Class<?> value()");
                }
                mixins.add(type.getQualifiedName().toString() + "=" + e.getTypeMirror());
                PackageElement packageElement = (PackageElement) asTypeElement(e.getTypeMirror()).getEnclosingElement();
                packageName = packageElement.toString();
            }
            if (packageName.startsWith("java.")) {
                throw new IllegalArgumentException("Cannot add @Mixins on Java classes!");
            }
            String className = ("Neighbor" + packageName.hashCode()).replace("-", "");
            String pn = packageName + "." + className;
            if (neighbors.get(packageName) == null) {
                try {
                    String fileName = packageName.isEmpty()
                            ? className
                            : pn;
                    neighbors.put(packageName, fileName);
                    JavaFileObject filerSourceFile = processingEnv.getFiler().createSourceFile(fileName);
                    try (Writer writer = filerSourceFile.openWriter()) {
                        if (!packageName.isEmpty()) {
                            writer.write("package " + packageName + ";");
                            writer.write("\n");
                        }
                        writer.write("class " + className + " {}");
                    } catch (Exception i) {
                        try {
                            filerSourceFile.delete();
                        } catch (Exception ignored) {
                        }
                        throw i;
                    }
                } catch (IOException ignored) {
                }
            }
        }
        try {
            FileObject object = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "mixins.properties");
            try (Writer writer = object.openWriter()) {
                writer.write(mixins.toString());
            }
        } catch (IOException ignored) {
        }
        try {
            FileObject object = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "mixins-neighbors.properties");
            try (Writer writer = object.openWriter()) {
                writer.write(neighbors.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("\n")));
            }
        } catch (IOException ignored) {}
        return false;
    }

    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = processingEnv.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }
}
