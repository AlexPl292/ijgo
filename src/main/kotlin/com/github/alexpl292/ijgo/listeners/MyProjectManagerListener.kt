package com.github.alexpl292.ijgo.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.alexpl292.ijgo.services.MyProjectService
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateEditingAdapter
import com.intellij.codeInsight.template.TemplateManagerListener
import com.intellij.codeInsight.template.impl.TemplateState
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiUtilBase
import com.intellij.ui.awt.RelativePoint
import java.awt.Color
import java.awt.Point
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.Timer

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
    }
}

class GoTemplateManagerListener : TemplateManagerListener {
    override fun templateStarted(state: TemplateState) {
        val editor = state.editor
        state.addTemplateStateListener(object : TemplateEditingAdapter() {
            override fun templateFinished(template: Template, brokenOff: Boolean) {
                println(state)
                super.templateFinished(template, brokenOff)
                val range = state.getSegmentRange(0)
                invokeLater {
                    if (template.segmentsCount > 0) {
                        val segmentOffset = range.startOffset
                        val file = PsiUtilBase.getPsiFile(editor.project!!, FileDocumentManager.getInstance().getFile(editor.document)!!)
                        val element = PsiUtilBase.getElementAtOffset(file, segmentOffset)
                        if (element is LeafPsiElement
                            && element.parent.node.elementType.debugName == "SPEC_TYPE"
                            && element.text.equals("gopher", ignoreCase = true)
                        ) {
                            runInEdt {
                                val popup = JBPopupFactory.getInstance()
                                    .createBalloonBuilder(createIcon())
                                    .setFillColor(Color(0, 0, 0, 0))
                                    .setBorderColor(Color(0, 0, 0, 0))
                                    .createBalloon()

                                val screenPoint = Point((editor.contentComponent.size.width), editor.contentComponent.height)
                                SwingUtilities.convertPointToScreen(screenPoint, editor.contentComponent)
                                popup.show(RelativePoint(screenPoint), Balloon.Position.above)
                                Timer(2000) {
                                    popup.hide()
                                }.start()
                            }
                        }
                    }
                }
            }
        })
    }
    fun createIcon(): JComponent {
        val icon = ImageIcon(this.javaClass.getResource("/animation.gif"))
        val jLabel = JLabel()
        jLabel.icon = icon
        return jLabel
    }
}
