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

import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.utils.StringUtils;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "******************** eos service processor start **********************");
        // String generatedClassName = "AutoGen" + System.currentTimeMillis();
        String generatedClassName = ParameterNamesFinder.GENERATED_HOLDER_CLASSNAME;
        StringBuilder sb = new StringBuilder("package com.sunsharing.eos.server.paranamer;\n"
            + "\n"
            + "import java.lang.reflect.Method;\n"
            + "import java.util.HashMap;\n"
            + "import java.util.Map;\n"
            + "import java.util.Arrays;\n"
            + "\n"
            + "public class " + generatedClassName + " implements ParameterNamesHolder {\n"
            + "\n"
            + "    public Map<String,Map<String,String[]>> container = new HashMap<String, Map<String, String[]>>();\n"
            + "\n"
            + "    public " + generatedClassName + "() { Map<String,String[]> methods = null;\n");
        // 初始化
        //获取所有被CustomAnnotation修饰的代码元素
        for (Element element : roundEnv.getElementsAnnotatedWith(EosService.class)) {
            if (element.getKind() != ElementKind.INTERFACE) {
                continue; // 退出处理
            }
            List<? extends Element> elementList = ((TypeElement) element).getEnclosedElements();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "find " + ((TypeElement) element).getQualifiedName() + " methods:" + elementList);
            sb.append("        methods = new HashMap<String, String[]>();\n"
                + "        container.put(\"" + ((TypeElement) element).getQualifiedName() + "\", methods);\n");
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
                    sb.append("        methods.put(\"" + ee.getSimpleName() + "\",new String[]{" + strParamNames + "});\n");
                }
            }
        }
        sb.append("    }\n"
            + "\n"
            + "    @Override\n"
            + "    public String[] getParameterNames(Class interfaces, Method method) {\n"
            + "        if(!container.containsKey(interfaces.getName())){\n"
            + "            throw new RuntimeException(\"该服务接口未能正常编译方法参数：\" + interfaces.getName());\n"
            + "        }\n"
            + "        String[] arr = container.get(interfaces.getName()).get(method.getName());\n"
            + "        return Arrays.copyOf(arr,arr.length);\n"
            + "    }\n"
            + "}");
        String generatedClass = ParameterNamesFinder.class.getPackage().getName() + "." + generatedClassName;
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
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "******************** eos service processor end **********************");
        return true;
    }

}
