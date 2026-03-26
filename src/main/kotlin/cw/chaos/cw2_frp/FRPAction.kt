package cw.chaos.cw2_frp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * 编辑器右键菜单
 */
class FRPActionGroup : ActionGroup(
    "Gen #frp",
    "生成文件引用指针并复制到剪贴板",
    null
) {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return (1..10).map { index ->
            FRPEditorAction(index)
        }.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        // 只在编辑器中有文件时显示
        e.presentation.isEnabledAndVisible = editor != null && virtualFile != null
    }
}

/**
 * 项目树右键菜单
 */
class FRPProjectTreeActionGroup : ActionGroup(
    "Gen #frp",
    "生成文件/文件夹引用指针并复制到剪贴板",
    null
) {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return (1..10).map { index ->
            FRPFileAction(index)
        }.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        // 在项目树中选中文件/文件夹时显示
        e.presentation.isEnabledAndVisible = virtualFile != null
    }
}

/**
 * 二级菜单：编辑器操作
 */
class FRPEditorAction(private val index: Int) : AnAction(
    "#frp$index",
    "生成 #frp$index 引用",
    null
) {
    private val generator = FRPGenerator()
    private val pathResolver = PathResolver()
    private val psiContextDetector = PsiContextDetector()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        // 获取选中范围
        val selectionModel = editor.selectionModel
        val document = editor.document

        val relativePath = pathResolver.resolveRelativePath(
            projectPath = project.basePath ?: return,
            filePath = virtualFile.path
        )

        val frpText = if (selectionModel.hasSelection()) {
            // 有选中文本 - 优先级最高
            val startLine = document.getLineNumber(selectionModel.selectionStart) + 1
            val endLine = document.getLineNumber(selectionModel.selectionEnd) + 1

            if (startLine == endLine) {
                generator.generateSingleLineSelection(index, relativePath, startLine)
            } else {
                generator.generateTextSelection(index, relativePath, startLine, endLine)
            }
        } else {
            // 无选中文本 - 检测 PSI 上下文
            when (val context = psiContextDetector.detect(project, editor)) {
                is PsiContext.MethodContext -> {
                    // 光标在方法上 - 生成方法引用
                    generator.generateMethodReference(
                        index,
                        relativePath,
                        context.methodName,
                        context.parameterTypes
                    )
                }
                is PsiContext.ClassContext -> {
                    // 光标在类/接口/enum 上 - 生成文件级引用
                    generator.generateFileReference(index, relativePath)
                }
                PsiContext.None -> {
                    // 未识别上下文 - 生成文件级引用
                    generator.generateFileReference(index, relativePath)
                }
            }
        }

        // 复制到剪贴板
        ClipboardService.copyToClipboard(frpText)

        // 显示通知
        showNotification(project, frpText)
    }

    private fun showNotification(project: Project, text: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("FRP Generator")
            .createNotification(
                "已复制到剪贴板",
                text,
                NotificationType.INFORMATION
            )
            .notify(project)
    }
}

/**
 * 二级菜单：项目树文件/文件夹操作
 */
class FRPFileAction(private val index: Int) : AnAction(
    "#frp$index",
    "生成 #frp$index 引用",
    null
) {
    private val generator = FRPGenerator()
    private val pathResolver = PathResolver()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val relativePath = pathResolver.resolveRelativePath(
            projectPath = project.basePath ?: return,
            filePath = virtualFile.path,
            isDirectory = virtualFile.isDirectory
        )

        val frpText = generator.generateFileReference(index, relativePath)

        // 复制到剪贴板
        ClipboardService.copyToClipboard(frpText)
        showNotification(project, frpText)
    }

    private fun showNotification(project: Project, text: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("FRP Generator")
            .createNotification(
                "已复制到剪贴板",
                text,
                NotificationType.INFORMATION
            )
            .notify(project)
    }
}
