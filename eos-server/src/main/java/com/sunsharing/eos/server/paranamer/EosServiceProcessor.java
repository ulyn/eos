/*
 * @(#) EosServiceProcessor
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2019
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2019-02-10 14:01:37
 */

package com.sunsharing.eos.server.paranamer;

import com.sunsharing.component.utils.crypto.Md5;
import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.utils.StringUtils;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.sunsharing.eos.common.annotation.EosService")
public class EosServiceProcessor extends AbstractProcessor {

    //生成的包名
    public final static String GENERATED_PKG = "eos";

    private ProcessingEnvironment processingEnv;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        List<Element> elements = new ArrayList();
        for (Element element : roundEnv.getElementsAnnotatedWith(EosService.class)) {
            if (element.getKind() != ElementKind.INTERFACE) {
                continue; // 退出处理
            }
            elements.add(element);
        }
        if (elements.isEmpty()) {
            return false;
        }
        Collections.sort(elements,new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
                return o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString());
            }
        });


        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "******************** eos service processor start **********************");

        // 初始化将参数名放入容器
        for (Element element : elements) {
            List<? extends Element> elementList = ((TypeElement) element).getEnclosedElements();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "find " + ((TypeElement) element).getQualifiedName() + " methods:" + elementList);

            String generatedClassName = ((TypeElement) element).getSimpleName().toString();
            String generatedClass = GENERATED_PKG + "." + ((TypeElement) element).getQualifiedName().toString();
            String pkg = generatedClass.substring(0,generatedClass.lastIndexOf("."));

            StringBuilder sb = new StringBuilder("package " + pkg + ";\n" +
                "\n" +
                "import com.sunsharing.eos.server.paranamer.ParameterNamesHolder;\n" +
                "\n" +
                "import java.lang.reflect.Method;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class " + generatedClassName + " implements ParameterNamesHolder {\n" +
                "\n" +
                "    private final Map<String,String[]> container = new HashMap<String, String[]>();\n" +
                "\n" +
                "    public " + generatedClassName + "(){\n");
            for (Element el : elementList) {
                if (el.getKind() == ElementKind.METHOD) {
                    ExecutableElement ee = (ExecutableElement) el;
                    List<String> paramNames = new ArrayList<String>();
                    for (Iterator<? extends VariableElement> it = ee.getParameters().iterator(); it.hasNext(); ) {
                        VariableElement param = it.next();
                        paramNames.add(param.getSimpleName().toString());
                    }
                    String strParamNames = StringUtils.join(paramNames, "\",\"");
                    if (!paramNames.isEmpty()) {
                        strParamNames = "\"" + strParamNames + "\"";
                    }
                    sb.append("        container.put(\"" + ee.getSimpleName() + "\",new String[]{" + strParamNames + "});\n");
                }
            }
            sb.append(
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    public Class getInterfaceClass() {\n" +
                "        return " + ((TypeElement) element).getQualifiedName().toString() + ".class;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String[] getParameterNames(Class interfaces, Method method) {\n" +
                "        String[] arr = container.get(method.getName());\n" +
                "        return Arrays.copyOf(arr,arr.length);\n" +
                "    }\n" +
                "\n" +
                "}\n"
            );

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "create class " + generatedClassName + "\n\n" + sb);
            OutputStream outputStream = null;
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(generatedClass);

                outputStream = source.openOutputStream();
                outputStream.write(sb.toString().getBytes(Charsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "******************** eos service processor end **********************");
        return true;
    }

}
