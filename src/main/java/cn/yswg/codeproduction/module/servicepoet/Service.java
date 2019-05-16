package cn.yswg.codeproduction.module.servicepoet;



import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class Service {
	Class<?> mapper;
	private String packageName = "com.test.service";
	private Class<?> beanClass;

	public Service(Class<?> mapper, String packageName) {
		this.mapper = mapper;
		this.packageName = packageName;

	}

	private MethodSpec buildMethod_inserSelective() {
		String name = getFirstLower(getBeanName());
		return buildBaseMethod("insertSelective", "insert", name);
	}

	/**
	 * 删除接口 根据key来删除
	 */
	private MethodSpec buildMethod_deleteByPrimaryKey() {
		return buildBaseMethod("deleteByPrimaryKey", "delete", "id");
	}

	private MethodSpec buildMethod_countByExample() {
		String name = getFirstLower(getBeanName());
		return buildBaseMethod("countByExample", "count", name);
	}

	private MethodSpec buildMethod_selectByPrimaryKey() {
		return buildBaseMethod("selectByPrimaryKey", "get", "id");
	}

	private MethodSpec buildMethod_selectByExample() {
		String name = getFirstLower(getBeanName());
		return buildBaseMethod("selectByExample", "list", name);
	}

	private MethodSpec buildMethod_updateByPrimaryKeySelective() {
		String name = getFirstLower(getBeanName());
		return buildBaseMethod("updateByPrimaryKeySelective", "update", name);
	}

	private MethodSpec buildMethod_page() {
		Class<?> bean = getBeanClass();
		String name = getFirstLower(getBeanName());
		MethodSpec.Builder builder = MethodSpec.methodBuilder("page").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.returns(Page.class);
		builder.addParameter(bean, name);
//		builder.addParameter(Integer.class, "start");
//		builder.addParameter(Integer.class, "limit");
		return builder.build();
	}

	private MethodSpec buildBaseMethod(String methodName, String bm, String... paramName) {
		Method method = getMethod(methodName);
		Parameter[] methodParam = method.getParameters();

		// 返回的return类型

		Type returnType = method.getGenericReturnType();

		MethodSpec.Builder builder = MethodSpec.methodBuilder(bm).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.returns(returnType);
		for (int i = 0; i < methodParam.length; i++) {
			if (methodParam[i].getType().toString().contains("Example")) {

				builder.addParameter(getBeanClass(), paramName[i]);
			} else {
				builder.addParameter(methodParam[i].getType(), paramName[i]);
			}
		}

		return builder.build();
	}

	private Builder buildServvice() {
		String name = getBeanName() + "Service";
		Builder servviceType = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
		return servviceType;
	}

	public JavaFile serviceFile() {
		Builder buildServvice = buildServvice();
		// buildServvice.addMethod(buildMethod_inser());
		buildServvice.addMethod(buildMethod_inserSelective());
		// buildServvice.addMethod(buildMethod_deleteByExample());
		buildServvice.addMethod(buildMethod_deleteByPrimaryKey());
		buildServvice.addMethod(buildMethod_countByExample());
		buildServvice.addMethod(buildMethod_selectByPrimaryKey());
		buildServvice.addMethod(buildMethod_selectByExample());
		// buildServvice.addMethod(buildMethod_updateByPrimaryKey());
		buildServvice.addMethod(buildMethod_updateByPrimaryKeySelective());
		// buildServvice.addMethod(buildMethod_updateByExample());
		// buildServvice.addMethod(buildMethod_updateByExampleSelective());
		buildServvice.addMethod(buildMethod_page());

		JavaFile javaFile = JavaFile.builder(packageName, buildServvice.build()).build();
		return javaFile;
	}

	/**
	 * 获取对应的bean的名称s
	 * @return
	 */
	protected String getBeanName() {
		String fullName = mapper.getName();
		String name = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.length());

		return name.replace("Mapper", "");// 去掉Mapper
	}

	protected String getClassName() {
		String beanName = getBeanName();
		return beanName + "Service";
	}

	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	protected String getFirstLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
	}

	protected Parameter getMethodParam(String methodName, int count) {
		Method method = getMethod(methodName);
		return method.getParameters()[count];
	}

	protected Method getMethod(String methodName) {
		Method[] methods = mapper.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	/**
	 * 获取对应的bean的class
	 * @return
	 */
	public Class<?> getBeanClass() {
		if (beanClass == null) {
			Method method = getMethod("insert");
			beanClass = method.getParameterTypes()[0];
		}
		return beanClass;
	}

}
