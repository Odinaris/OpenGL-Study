package com.odinaris.lib_annotation_processor;

import static com.google.common.reflect.Reflection.getPackageName;

import com.google.auto.service.AutoService;
import com.odinaris.lib_annotation.FunctionConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
public class FunctionProcessor extends AbstractProcessor {

    private final static String PACKAGE_NAME = "com.odinaris.opengldemo";
    private final static String PACKAGE_NAME_PRESENTER = "com.odinaris.opengldemo.presenter";
    // 业务模块的包名和类名
    private static final String BUSINESS_PACKAGE = "com.odinaris.opengldemo.presenter";
    private static final ClassName BASE_PRESENTER = ClassName.get(BUSINESS_PACKAGE, "BasePresenter");
    private static final ClassName MAIN_CATEGORY_PRESENTER = ClassName.get(BUSINESS_PACKAGE, "MainCategoryPresenter");
    private static final ClassName PRESENTER_FACTORY = ClassName.get(BUSINESS_PACKAGE, "PresenterFactory");

    private final Map<Integer, String> mIdClassMap = new HashMap<>();
    private List<TypeElement> classes;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FunctionConfig.class);
        //process会被调用三次，只有一次是可以处理InjectView注解的，原因不明
        if (elements.isEmpty()) {
            return true;
        }
        // 遍历所有标注了 @PresenterId 的类
        for (Element element : roundEnv.getElementsAnnotatedWith(FunctionConfig.class)) {
            if (element instanceof TypeElement typeElement) {
                int presenterId = typeElement.getAnnotation(FunctionConfig.class).functionId();
                String presenterClassName = typeElement.getQualifiedName().toString();
//                String presenterClassName = typeElement.getSimpleName().toString();

                // 生成对应的工厂类
                generatePresenterFactory(presenterClassName);
            }
        }

        // 生成 PresenterRegistry 类
        generatePresenterRegistry(roundEnv);

        return true;
    }

    private void generatePresenterFactory(String presenterClassName) {
        // 工厂类的类名
        String factoryClassName = presenterClassName + "Factory";

        // 生成工厂类
        TypeSpec factoryClass = TypeSpec.classBuilder(ClassName.bestGuess(factoryClassName).simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(PRESENTER_FACTORY)
                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(BASE_PRESENTER)
                        .addParameter(MAIN_CATEGORY_PRESENTER, "mainCategoryPresenter")
                        .addStatement("return new $T(mainCategoryPresenter)", ClassName.bestGuess(presenterClassName))
                        .build())
                .build();

        // 生成 Java 文件
        JavaFile javaFile = JavaFile.builder(getPackageName(presenterClassName), factoryClass)
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePresenterRegistry(RoundEnvironment roundEnv) {
        // 创建一个 Map 用于存储 Presenter ID 和工厂类的映射
        MethodSpec.Builder registerMethodBuilder = MethodSpec.methodBuilder("registerPresenters")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(void.class);

        // 遍历所有标注了 @PresenterId 的类
        for (Element element : roundEnv.getElementsAnnotatedWith(FunctionConfig.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                int presenterId = typeElement.getAnnotation(FunctionConfig.class).functionId();
                String presenterClassName = typeElement.getQualifiedName().toString();

                // 生成注册代码
                registerMethodBuilder.addStatement("presenterMap.put($S, new $T())",
                        presenterId, ClassName.bestGuess(presenterClassName + "Factory"));
            }
        }

        // 生成 PresenterRegistry 类
        TypeSpec presenterRegistry = TypeSpec.classBuilder("PresenterRegistry")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(
                                ParameterizedTypeName.get(ClassName.get("java.util", "Map"),
                                        ClassName.get(String.class),
                                        PRESENTER_FACTORY),
                                "presenterMap",
                                Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T<>()", ClassName.get("java.util", "HashMap"))
                        .build())
                .addMethod(registerMethodBuilder.build())
                .addMethod(MethodSpec.methodBuilder("createPresenter")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(BASE_PRESENTER)
                        .addParameter(MAIN_CATEGORY_PRESENTER, "mainCategoryPresenter")
                        .addParameter(ClassName.get(String.class), "id")
                        .addStatement("$T factory = presenterMap.get(id)", PRESENTER_FACTORY)
                        .beginControlFlow("if (factory != null)")
                        .addStatement("return factory.create(mainCategoryPresenter)")
                        .endControlFlow()
                        .addStatement("throw new $T($S + id)", ClassName.get("java.lang", "IllegalArgumentException"), "Unknown presenter id: ")
                        .build())
                .addStaticBlock(CodeBlock.builder()
                        .addStatement("registerPresenters()")
                        .build())
                .build();

        // 生成 Java 文件
        JavaFile javaFile = JavaFile.builder(BUSINESS_PACKAGE, presenterRegistry)
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPackageName(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot > 0 ? className.substring(0, lastDot) : "";
    }
}