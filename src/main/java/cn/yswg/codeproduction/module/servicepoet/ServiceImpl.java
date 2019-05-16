package cn.yswg.codeproduction.module.servicepoet;


import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class ServiceImpl {
	Class<?> mapper;
	private String packageName = "";
	private Class<?> beanClass;
	private Class<?> beanExampleClass;
	private FieldSpec fieldMapper;

	public ServiceImpl(Class<?> mapper, String packageName) {
		this.mapper = mapper;
		this.packageName = packageName;
		// 初始化
		getBeanClass();
		getBeanExampleClass();
		fieldMapper = buildField_mapper();
	}

	/**
	 * 创建对应的mapper字段
	 * @return
	 */
	private FieldSpec buildField_mapper() {
		String name = getFirstLower(getBeanName()) + "Mapper";
		FieldSpec.Builder builder = FieldSpec.builder(mapper, name, Modifier.PUBLIC);
		builder.addAnnotation(Autowired.class);
		return builder.build();
	}



	private MethodSpec.Builder buildBaseMethod(String methodName, String bm, String... paramName) {
		Method method = getMethod(methodName);
		Parameter[] methodParam = method.getParameters();

		// 返回的return类型
		Type returnType = method.getGenericReturnType();

		MethodSpec.Builder builder = MethodSpec.methodBuilder(bm).addModifiers(Modifier.PUBLIC).returns(returnType);
		for (int i = 0; i < methodParam.length; i++) {

			if (methodParam[i].getType().toString().contains("Example")) {

				builder.addParameter(getBeanClass(), paramName[i]);
			} else {
				builder.addParameter(methodParam[i].getType(), paramName[i]);
			}

		}

		return builder;
	}


	/**
	 * 创建service 实现类 xxxServiceImpl
	 *
	 * @return
	 */
	private Builder buildServviceImpl() {
		String name = getBeanName() + "ServiceImpl";

		Builder servviceType = TypeSpec.classBuilder(name).addSuperinterface(ClassName.bestGuess(getServicePackPath()))
				.addModifiers(Modifier.PUBLIC).addAnnotation(Service.class);
		return servviceType;
	}

	public JavaFile serviceFile() {
		Builder buildServvice = buildServviceImpl();
		buildServvice.addField(getFieldMapper());
		// buildServvice.addMethod(buildMethod_inser());
		buildServvice.addMethod(buildMethod_inserSelective());
		// buildServvice.addMethod(buildMethod_deleteByExample());
		buildServvice.addMethod(buildMethod_deleteByPrimaryKey());

		buildServvice.addMethod(buildMethod_countByExample());
		buildServvice.addMethod(buildMethod_selectByPrimaryKey());
		buildServvice.addMethod(buildMethod_list());
		// buildServvice.addMethod(buildMethod_updateByPrimaryKey());
		buildServvice.addMethod(buildMethod_updateByPrimaryKeySelective());

		// buildServvice.addMethod(buildMethod_updateByExample());
		// buildServvice.addMethod(buildMethod_updateByExampleSelective());
		buildServvice.addMethod(buildMethod_page());
		buildServvice.addMethod(buildGetExampleByBean());
		JavaFile javaFile = JavaFile.builder(packageName, buildServvice.build()).build();
		return javaFile;
	}


	private MethodSpec buildGetExampleByBean() {
		Class<?> beanExample = getBeanExampleClass();
		Class<?> beanClass = getBeanClass();
		String beanName = getFirstLower(getBeanName());
		MethodSpec.Builder builder = MethodSpec.methodBuilder("get" + getBeanName() + "Example");
		builder.addModifiers(Modifier.PRIVATE);
		builder.addParameter(getBeanClass(), beanName);
		builder.addStatement("$T example = new $T() ", beanExample, beanExample);
		builder.addStatement("$T.Criteria criteria = example.createCriteria()", beanExample);
		CodeBlock.Builder code1 = CodeBlock.builder();
		code1.add(CodeBlock.of("if ($L==null){\n", beanName));
		code1.add(CodeBlock.of("    return example;\n")).add(CodeBlock.of("}\n"));
		builder.addCode(code1.build());

		Field[] fields = beanClass.getDeclaredFields();
		for (Field field : fields) {
			String fieldUp = getFirstUpper(field.getName());
			CodeBlock.Builder code2 = CodeBlock.builder();
			if (field.getType().equals(String.class)) {
				code2.add(CodeBlock.of("if (!$T.isEmpty($L.get" + fieldUp + "())){\n", StringUtils.class, beanName));
			} else {
				code2.add(CodeBlock.of("if ($L.get" + fieldUp + "()!=null){\n", beanName));
			}

			code2.add(CodeBlock.of("    criteria.and" + fieldUp + "EqualTo($L.get" + fieldUp + "());\n", beanName))
					.add(CodeBlock.of("}\n"));
			builder.addCode(code2.build());
		}
		builder.addStatement("return example");
		builder.returns(beanExample);
		return builder.build();
	}

	private MethodSpec buildMethod_inserSelective() {
		String name = getFirstLower(getBeanName());
		MethodSpec.Builder builder = buildBaseMethod("insertSelective", "insert", name);
		builder.addStatement("return " + getFieldMapperName() + ".insertSelective($L)", name);
		// builder.build().parameters.get(0).getClass()
		//return builder.build();
		return endBuild(builder);
	}

	/**
	 * 删除接口 根据key来删除
	 */
	private MethodSpec buildMethod_deleteByPrimaryKey() {
		MethodSpec.Builder builder = buildBaseMethod("deleteByPrimaryKey", "delete", "id");
		builder.addStatement("return " + getFieldMapperName() + ".deleteByPrimaryKey($L)", "id");
		//return builder.build();
		return endBuild(builder);
	}

	private MethodSpec buildMethod_countByExample() {
		String name = getFirstLower(getBeanName());

		MethodSpec.Builder builder = buildBaseMethod("countByExample", "count", name);
		builder.addStatement("$T example = get" + getBeanName() + "Example($L)", beanExampleClass, name);
		builder.addStatement("return " + getFieldMapperName() + ".countByExample(example)");
		//return builder.build();
		return endBuild(builder);
	}

	private MethodSpec buildMethod_selectByPrimaryKey() {

		MethodSpec.Builder builder = buildBaseMethod("selectByPrimaryKey", "get", "id");
		builder.addStatement("return " + getFieldMapperName() + ".selectByPrimaryKey(id)");
		//return builder.build();
		return endBuild(builder);
	}

	private MethodSpec buildMethod_page() {
		Class<?> bean = getBeanClass();
		String name = getFirstLower(getBeanName());
		MethodSpec.Builder builder = MethodSpec.methodBuilder("page").addModifiers(Modifier.PUBLIC).returns(Page.class);
		builder.addParameter(bean, name);
		// builder.addParameter(Integer.class, "start");
		// builder.addParameter(Integer.class, "limit");

		builder.addStatement("$T example = get" + getBeanName() + "Example($L)", beanExampleClass, name);
		builder.addStatement("int count = " + getFieldMapperName() + ".countByExample(example)");
		builder.addStatement("example.setLimitStart("+name+".getStart())");
		builder.addStatement("example.setLimitEnd("+name+".getLimit())");
		builder.addStatement("return new Page(count, " + getFieldMapperName() + ".selectByExample(example))");
		//return builder.build();
		return endBuild(builder);
	}

	private MethodSpec buildMethod_list() {
		String name = getFirstLower(getBeanName());
		MethodSpec.Builder builder = buildBaseMethod("selectByExample", "list", name);
		builder.addStatement("$T example = get" + getBeanName() + "Example($L)", beanExampleClass, name);
		builder.addStatement("return " + getFieldMapperName() + ".selectByExample(example)");
		//return builder.build();
		return endBuild(builder);
	}

	private MethodSpec buildMethod_updateByPrimaryKeySelective() {
		String name = getFirstLower(getBeanName());
		MethodSpec.Builder builder = buildBaseMethod("updateByPrimaryKeySelective", "update", name);
		builder.addStatement("return " + getFieldMapperName() + ".updateByPrimaryKeySelective(" + name + ")");

		//return builder.build();
		return endBuild(builder);
	}

	/**
	 * 跟实现类方法加上Override主键
	 * @param builder
	 * @return
	 */
	public MethodSpec endBuild(MethodSpec.Builder builder){
		builder.addAnnotation(Override.class);
		return builder.build();
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
		return beanName + "ServiceImpl";
	}

	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	protected String getFirstLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
	}

	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	protected String getFirstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
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

	private String getServicePackPath() {
		String servicePack = packageName.substring(0, packageName.lastIndexOf("."));
		return servicePack + "." + getBeanName() + "Service";
	}

	public FieldSpec getFieldMapper() {
		if (fieldMapper == null) {
			fieldMapper = buildField_mapper();
		}
		return fieldMapper;
	}

	private String getFieldMapperName() {
		return getFieldMapper().name;
	}

	public void setFieldMapper(FieldSpec fieldMapper) {
		this.fieldMapper = fieldMapper;
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

	/**
	 * 获取对应的beanExample的class
	 * @return
	 */
	public Class<?> getBeanExampleClass() {
		if (beanExampleClass == null) {
			Method method = getMethod("selectByExample");
			beanExampleClass = method.getParameterTypes()[0];
		}
		return beanExampleClass;
	}

}
