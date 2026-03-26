package cw.chaos.cw2_frp

/**
 * 方法签名格式化器
 * 将方法名和参数类型列表格式化为方法签名字符串
 */
class MethodSignatureFormatter {

    /**
     * 格式化方法签名
     *
     * @param methodName 方法名
     * @param parameterTypes 参数类型列表
     * @return 格式化的签名，如 "resolveValue(String, int)"
     */
    fun format(methodName: String, parameterTypes: List<String>): String {
        val params = parameterTypes.joinToString(", ")
        return "$methodName($params)"
    }
}
