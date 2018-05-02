package cn.com.leanvision.compiler.spf;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.com.leanvision.annotation.spf.LvSpfConfig;
import cn.com.leanvision.annotation.spf.LvSpfPreferences;
import cn.com.leanvision.compiler.common.ErrorReporter;

/********************************
 * Created by lvshicheng on 2016/12/4.
 ********************************/
@SupportedAnnotationTypes("cn.com.leanvision.annotation.spf.LvSpfPreferences")
@AutoService(Processor.class)
public class SpfProcessor extends AbstractProcessor {

  private ErrorReporter mErrorReporter;
  private Types         mTypeUtils;
  private Elements      elementUtils;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    mErrorReporter = new ErrorReporter(processingEnvironment);
    mTypeUtils = processingEnvironment.getTypeUtils();
    elementUtils = processingEnvironment.getElementUtils();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

    Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(LvSpfPreferences.class);
    ImmutableList<TypeElement> types = new ImmutableList.Builder<TypeElement>()
        .addAll(ElementFilter.typesIn(annotatedElements))
        .build();
    for (TypeElement type : types) {
      processType(type);
    }
    return true;
  }

  // Handle every class with LvPreference annotation
  private void processType(TypeElement typeElement) {
    LvSpfPreferences lvPreferences = typeElement.getAnnotation(LvSpfPreferences.class);
    if (lvPreferences == null) {
      mErrorReporter.abortWithError("annotation processor for @AutoParcel was invoked with a type annotated differently; compiler bug? O_o", typeElement);
    }

    if (typeElement.getKind() != ElementKind.CLASS) {
      mErrorReporter.abortWithError("@" + LvSpfPreferences.class.getName() + " only applies to classes", typeElement);
    }

    // 获取该类的全部成员，包括
    List<? extends Element> members = elementUtils.getAllMembers(typeElement);

    List<MethodSpec> methodSpecs = new ArrayList<>();
    Set<Element> inClassElements = new HashSet<>();


    for (Element item : members) {
      // 忽略除了成员方法外的元素
      //a method, constructor, or initializer (static or instance) of a class or interface
      if (!(item instanceof ExecutableElement)) {
        continue;
      }
      //忽略final、static 方法
      if (item.getModifiers().contains(Modifier.FINAL) || item.getModifiers().contains(Modifier.STATIC)) {
        continue;
      }
      ExecutableElement executableElement = (ExecutableElement) item;
      String name = item.getSimpleName().toString();

      if (name.equals("getClass")) {
        continue;
      }

      boolean getter = false;
      if (name.startsWith("get") || name.startsWith("is")) {
        getter = true;
      } else if (name.startsWith("set")) {
        getter = false;
      } else {
        continue;
      }

      String fieldName = name.replaceFirst("get|is|set", "");
      fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

      Element fieldElement = getElement(members, fieldName);
      if (fieldElement == null) {
        continue;
      }

      LvSpfConfig annotation = fieldElement.getAnnotation(LvSpfConfig.class);

      if (annotation != null && !annotation.save()) {
        continue;
      }
      String modName;
      boolean isDouble = false;
      boolean isObject = false;
      TypeName type = TypeName.get(fieldElement.asType());
      if (type.equals(TypeName.BOOLEAN)) {
        if (getter) {
          modName = "getBoolean";
        } else {
          modName = "putBoolean";
        }
      } else if (type.equals(TypeName.INT)) {
        if (getter) {
          modName = "getInt";
        } else {
          modName = "putInt";
        }
      } else if (type.equals(TypeName.DOUBLE)) {
        if (getter) {
          modName = "getFloat";
        } else {
          modName = "putFloat";
        }
        isDouble = true;
      } else if (type.equals(TypeName.FLOAT)) {
        if (getter) {
          modName = "getFloat";
        } else {
          modName = "putFloat";
        }
      } else if (type.equals(TypeName.LONG)) {
        if (getter) {
          modName = "getLong";
        } else {
          modName = "putLong";
        }
      } else if (type.equals(TypeName.get(String.class))) {
        if (getter) {
          modName = "getString";
        } else {
          modName = "putString";
        }
      } else {
        if (getter) {
          modName = "getString";
        } else {
          modName = "putString";
        }

        if (annotation != null && annotation.preferences()) {
          inClassElements.add(fieldElement);
          continue;
        }
        isObject = true;
      }
      boolean globalField = true;
      if (annotation != null && !annotation.global()) {
        globalField = false;
      }

      if (name.startsWith("set")) {
        if (isObject) {
          MethodSpec setMethod = MethodSpec.overriding(executableElement)
              .addStatement(String.format("mEdit.putString(getRealKey(\"%s\",%b), AptPreferencesManager.getAptParser().serialize(%s)).apply()", fieldName, globalField, fieldName))
              .build();
          methodSpecs.add(setMethod);
          continue;
        }
        MethodSpec setMethod;
        String fieldNameModify = fieldName;

        if (annotation != null && annotation.encryption()) {
          if (!type.equals(TypeName.get(String.class))) {
            mErrorReporter.abortWithError("@" + LvSpfPreferences.class.getName() + " only string can be encrypted!", typeElement);
          }
          fieldNameModify = String.format("AptPreferencesManager.getSpfEncryption().encodeStr(%s)", fieldName);
        }

        if (annotation != null && annotation.commit()) {
          if (isDouble) {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), (float)%s).commit()", modName, fieldName, globalField, fieldName)).build();
          } else {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), %s).commit()", modName, fieldName, globalField, fieldNameModify)).build();
          }
        } else {
          if (isDouble) {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), (float)%s).apply()", modName, fieldName, globalField, fieldName)).build();
          } else {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), %s).apply()", modName, fieldName, globalField, fieldNameModify)).build();
          }
        }
        methodSpecs.add(setMethod);
      } else {
        if (isObject) {
          TypeName className = ClassName.get(fieldElement.asType());
          MethodSpec setMethod = MethodSpec.overriding(executableElement)
              .addStatement(String.format("String text = mPreferences.getString(getRealKey(\"%s\",%b), null)", fieldName, globalField))
              .addStatement("Object object = null")
              .addCode("if (text != null){\n" +
                  "   object = AptPreferencesManager.getAptParser().deserialize($T.class,text);\n" +
                  "}\n" +
                  "if (object != null){\n" +
                  "   return ($T) object;\n" +
                  "}\n", className, className)
              .addStatement(String.format("return super.%s()", executableElement.getSimpleName()))
              .build();
          methodSpecs.add(setMethod);
          continue;
        }


        String fieldNameModify = null;
        if (annotation != null && annotation.encryption()) {
          if (!type.equals(TypeName.get(String.class))) {
            mErrorReporter.abortWithError("@" + LvSpfPreferences.class.getName() + " only string can be encrypted!", typeElement);
          }
          fieldNameModify = String.format("AptPreferencesManager.getSpfEncryption().decodeStr(%s)", fieldName);
        }

        if (isDouble) {
          MethodSpec setMethod = MethodSpec.overriding(executableElement)
              .addStatement(String.format("return mPreferences.%s(getRealKey(\"%s\",%b), (float)super.%s())", modName, fieldName, globalField, name))
              .build();

          methodSpecs.add(setMethod);
        } else {
          MethodSpec setMethod;
          if (fieldNameModify == null) {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("return mPreferences.%s(getRealKey(\"%s\",%b), super.%s())", modName, fieldName, globalField, name))
                .build();
          } else {
            setMethod = MethodSpec.overriding(executableElement)
                .addStatement(String.format("String %s = mPreferences.%s(getRealKey(\"%s\",%b), super.%s())", fieldName, modName, fieldName, globalField, name))
                .addCode(String.format("return %s;\n", fieldNameModify))
                .build();
          }

          methodSpecs.add(setMethod);
        }
      }
    }
    TypeName targetClassName = ClassName.get(getPackageName(typeElement), typeElement.getSimpleName() + "Preferences");
    MethodSpec getMethodSpec2 = MethodSpec.methodBuilder("get")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(targetClassName)
        .addCode("if (sInstance == null){\n" +
            "   synchronized ($T.class){\n" +
            "      if (sInstance == null){\n" +
            "          sInstance = new $T();\n" +
            "      }\n" +
            "   }\n" +
            "}\n" +
            "return sInstance;\n", targetClassName, targetClassName)
        .build();

    MethodSpec clearMethodSpec = MethodSpec.methodBuilder("clear")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.VOID)
        .addStatement("mEdit.clear().commit()")
        .build();

    MethodSpec getRealKeyMethodSpec = MethodSpec.methodBuilder("getRealKey")
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addParameter(String.class, "key")
        .addParameter(TypeName.BOOLEAN, "global")
        .addStatement("return global ? key : AptPreferencesManager.getUserInfo() + key")
        .build();

    List<TypeSpec> typeSpecs = getInClassTypeSpec(inClassElements);
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("mPreferences = $T.getContext().getSharedPreferences($S, 0)", ClassName.get("com.renren0351.model.storage", "AptPreferencesManager"), typeElement.getSimpleName())
        .addStatement("mEdit = mPreferences.edit()");
    for (TypeSpec typeSpec : typeSpecs) {
      constructor.addStatement(String.format("this.set%s(new %s())", typeSpec.name.replace("Preferences", ""), typeSpec.name));
    }

    FieldSpec fieldSpec = FieldSpec.builder(targetClassName, "sInstance", Modifier.PRIVATE, Modifier.STATIC)
        .initializer("new $T()", targetClassName)
        .build();
    TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "Preferences")
        .superclass(TypeName.get(typeElement.asType()))
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addMethods(methodSpecs)
        .addMethod(getMethodSpec2)
        .addMethod(constructor.build())
        .addMethod(clearMethodSpec)
        .addMethod(getRealKeyMethodSpec)
        .addField(ClassName.get("android.content", "SharedPreferences", "Editor"), "mEdit", Modifier.PRIVATE, Modifier.FINAL)
        .addField(ClassName.get("android.content", "SharedPreferences"), "mPreferences", Modifier.PRIVATE, Modifier.FINAL)
        .addField(fieldSpec)
        .addTypes(typeSpecs)
        .build();
    JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec).build();

    try {
      javaFile.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<TypeSpec> getInClassTypeSpec(Set<Element> inClassElements) {
    List<TypeSpec> typeSpecs = new ArrayList<>();
    for (Element element : inClassElements) {
      TypeElement typeElement = elementUtils.getTypeElement(TypeName.get(element.asType()).toString());
      boolean globalField = true;
      LvSpfConfig tmp = element.getAnnotation(LvSpfConfig.class);
      if (tmp != null && !tmp.global()) {
        globalField = false;
      }
      List<? extends Element> members = elementUtils.getAllMembers(typeElement);
      List<MethodSpec> methodSpecs = new ArrayList<>();
      for (Element item : members) {

        if (item instanceof ExecutableElement) {
          ExecutableElement executableElement = (ExecutableElement) item;
          String name = item.getSimpleName().toString();
          if (name.equals("getClass")) {
            continue;
          }
          //忽略final、static 方法
          if (executableElement.getModifiers().contains(Modifier.FINAL) || executableElement.getModifiers().contains(Modifier.STATIC)) {
            continue;
          }
          if (!name.startsWith("get") && !name.startsWith("is") && !name.startsWith("set")) {
            continue;
          }
          String fieldName = name.replaceFirst("get|is|set", "");
          fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

          Element fieldElement = getElement(members, fieldName);
          LvSpfConfig annotation = item.getAnnotation(LvSpfConfig.class);
          if (annotation != null && !annotation.save()) {
            continue;
          }
          TypeName type = TypeName.get(fieldElement.asType());


          if (name.startsWith("set")) {
            String modName;
            boolean isDouble = false;
            if (type.equals(TypeName.BOOLEAN)) {
              modName = "putBoolean";
            } else if (type.equals(TypeName.INT)) {
              modName = "putInt";
            } else if (type.equals(TypeName.DOUBLE)) {
              modName = "putFloat";
              isDouble = true;
            } else if (type.equals(TypeName.FLOAT)) {
              modName = "putFloat";
            } else if (type.equals(TypeName.LONG)) {
              modName = "putLong";
            } else {
              modName = "putString";
            }
            MethodSpec setMethod;

            if (annotation != null && annotation.commit()) {
              if (isDouble) {
                setMethod = MethodSpec.overriding(executableElement)
                    .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), (float)%s).commit()", modName, typeElement.getSimpleName() + "." + fieldName, globalField, fieldName)).build();
              } else {
                setMethod = MethodSpec.overriding(executableElement)
                    .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), %s).commit()", modName, typeElement.getSimpleName() + "." + fieldName, globalField, fieldName)).build();
              }
            } else {
              if (isDouble) {
                setMethod = MethodSpec.overriding(executableElement)
                    .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), (float)%s).apply()", modName, typeElement.getSimpleName() + "." + fieldName, globalField, fieldName)).build();
              } else {
                setMethod = MethodSpec.overriding(executableElement)
                    .addStatement(String.format("mEdit.%s(getRealKey(\"%s\",%b), %s).apply()", modName, typeElement.getSimpleName() + "." + fieldName, globalField, fieldName)).build();
              }
            }


            methodSpecs.add(setMethod);
          } else if (name.startsWith("get") || name.startsWith("is")) {
            String modName;
            boolean isDouble = false;
            if (type.equals(TypeName.BOOLEAN)) {
              modName = "getBoolean";
            } else if (type.equals(TypeName.INT)) {
              modName = "getInt";
            } else if (type.equals(TypeName.DOUBLE)) {
              modName = "getFloat";
              isDouble = true;
            } else if (type.equals(TypeName.FLOAT)) {
              modName = "getFloat";
            } else if (type.equals(TypeName.LONG)) {
              modName = "getLong";
            } else {
              modName = "getString";
            }


            if (isDouble) {
              MethodSpec setMethod = MethodSpec.overriding(executableElement)
                  .addStatement(String.format("return mPreferences.%s(getRealKey(\"%s\",%b), (float)super.%s())", modName, typeElement.getSimpleName() + "." + fieldName, globalField, name))
                  .build();

              methodSpecs.add(setMethod);
            } else {
              MethodSpec setMethod = MethodSpec.overriding(executableElement)
                  .addStatement(String.format("return mPreferences.%s(getRealKey(\"%s\",%b), super.%s())", modName, typeElement.getSimpleName() + "." + fieldName, globalField, name))
                  .build();

              methodSpecs.add(setMethod);
            }

          }

        }
      }
      TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "Preferences")
          .superclass(TypeName.get(element.asType()))
          .addModifiers(Modifier.PRIVATE)
          .addMethods(methodSpecs)
          .build();
      typeSpecs.add(typeSpec);
    }
    return typeSpecs;
  }

  private Element getElement(List<? extends Element> members, String fieldName) {
    for (Element item : members) {
      if (item.getSimpleName().toString().equals(fieldName)) {
        return item;
      }
    }
    return null;
  }

  private String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }
}
