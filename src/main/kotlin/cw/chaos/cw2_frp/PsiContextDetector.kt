package cw.chaos.cw2_frp

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * PSI 上下文检测结果
 */
sealed class PsiContext {
    data class MethodContext(
        val methodName: String,
        val parameterTypes: List<String>
    ) : PsiContext()

    data class ClassContext(val className: String) : PsiContext()
    object None : PsiContext()
}

/**
 * PSI 上下文检测器
 * 检测光标所在的 PSI 元素类型（方法、类等）
 */
class PsiContextDetector {

    /**
     * 检测光标位置的 PSI 上下文
     *
     * @param project 项目
     * @param editor 编辑器
     * @return PSI 上下文（方法、类或无）
     */
    fun detect(project: Project, editor: Editor): PsiContext {
        val offset = editor.caretModel.offset
        val psiFile = com.intellij.psi.PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
            ?: return PsiContext.None

        val elementAtCaret = psiFile.findElementAt(offset) ?: return PsiContext.None

        // 优先检测方法
        val methodContext = detectMethod(elementAtCaret)
        if (methodContext != null) {
            return methodContext
        }

        // 检测类/接口/enum
        val classContext = detectClass(elementAtCaret)
        if (classContext != null) {
            return classContext
        }

        return PsiContext.None
    }

    private fun detectMethod(element: PsiElement): PsiContext.MethodContext? {
        // Java 方法
        val javaMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
        if (javaMethod != null) {
            return PsiContext.MethodContext(
                methodName = javaMethod.name,
                parameterTypes = javaMethod.parameterList.parameters.map { it.type.presentableText }
            )
        }

        // Kotlin 函数
        val kotlinFunction = PsiTreeUtil.getParentOfType(element, KtFunction::class.java)
        if (kotlinFunction != null && kotlinFunction.name != null) {
            return PsiContext.MethodContext(
                methodName = kotlinFunction.name!!,
                parameterTypes = kotlinFunction.valueParameters.map { param ->
                    param.typeReference?.text ?: "Any"
                }
            )
        }

        return null
    }

    private fun detectClass(element: PsiElement): PsiContext.ClassContext? {
        // Java 类/接口/enum
        val javaClass = PsiTreeUtil.getParentOfType(element, PsiClass::class.java)
        if (javaClass != null) {
            return PsiContext.ClassContext(javaClass.name ?: return null)
        }

        // Kotlin 类/object
        val ktClass = PsiTreeUtil.getParentOfType(element, KtClass::class.java)
        if (ktClass != null) {
            return PsiContext.ClassContext(ktClass.name ?: return null)
        }

        val ktObject = PsiTreeUtil.getParentOfType(element, KtObjectDeclaration::class.java)
        if (ktObject != null) {
            return PsiContext.ClassContext(ktObject.name ?: return null)
        }

        return null
    }
}
