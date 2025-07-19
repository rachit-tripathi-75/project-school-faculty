package com.example.apsforfaculty.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.databinding.ItemAttachmentBinding
import com.example.apsforfaculty.utils.FileUtils

class AttachmentAdapter(
    private val attachmentsList: MutableList<Uri>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    inner class AttachmentViewHolder(private val binding: ItemAttachmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            binding.apply {
                // Get file name from URI
                val fileName = FileUtils.getFileName(root.context, uri) ?: "Unknown file"
                val fileSize = FileUtils.getFileSize(root.context, uri)
                val fileExtension = FileUtils.getFileExtension(fileName)

                // Set file info
                tvFileName.text = fileName
                tvFileSize.text = FileUtils.formatFileSize(fileSize)

                // Set appropriate icon based on file type
                ivFileIcon.setImageResource(getFileIcon(fileExtension))

                // Set remove button click listener
                btnRemoveAttachment.setOnClickListener {
                    onRemoveClick(position)
                }

                // Set file type indicator color
                viewFileTypeIndicator.setBackgroundColor(getFileTypeColor(fileExtension))
            }
        }

        private fun getFileIcon(extension: String): Int {
            return when (extension.lowercase()) {
                "pdf" -> R.drawable.ic_file_pdf
                "doc", "docx" -> R.drawable.ic_file_doc
                "jpg", "jpeg", "png", "gif" -> R.drawable.ic_file_image
                "txt" -> R.drawable.ic_file_text
                "xls", "xlsx" -> R.drawable.ic_file_excel
                "ppt", "pptx" -> R.drawable.ic_file_powerpoint
                else -> R.drawable.ic_file_generic
            }
        }

        private fun getFileTypeColor(extension: String): Int {
            val context = binding.root.context
            return when (extension.lowercase()) {
                "pdf" -> context.getColor(R.color.red_600)
                "doc", "docx" -> context.getColor(R.color.blue_600)
                "jpg", "jpeg", "png", "gif" -> context.getColor(R.color.green_600)
                "txt" -> context.getColor(R.color.gray_600)
                "xls", "xlsx" -> context.getColor(R.color.green_600)
                "ppt", "pptx" -> context.getColor(R.color.orange_600)
                else -> context.getColor(R.color.gray_500)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val binding = ItemAttachmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttachmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(attachmentsList[position], position)
    }

    override fun getItemCount(): Int = attachmentsList.size

    fun addAttachments(newAttachments: List<Uri>) {
        val startPosition = attachmentsList.size
        attachmentsList.addAll(newAttachments)
        notifyItemRangeInserted(startPosition, newAttachments.size)
    }

    fun removeAttachment(position: Int) {
        if (position < attachmentsList.size) {
            attachmentsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, attachmentsList.size)
        }
    }

    fun getAttachments(): List<Uri> = attachmentsList.toList()

    fun clearAttachments() {
        val size = attachmentsList.size
        attachmentsList.clear()
        notifyItemRangeRemoved(0, size)
    }
}