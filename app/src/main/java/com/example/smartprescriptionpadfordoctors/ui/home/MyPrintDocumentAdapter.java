package com.example.smartprescriptionpadfordoctors.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintSet;

import com.example.smartprescriptionpadfordoctors.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
    private Context context;
    private View contentLayout;
    private PrintAttributes printAttributes;


    public MyPrintDocumentAdapter(Context context, View contentLayout) {
        this.context = context;
        this.contentLayout = contentLayout;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("Patient ID");
        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build();
        // Save the print attributes for later use
        printAttributes = newAttributes;

        callback.onLayoutFinished(builder.build(), newAttributes.equals(oldAttributes));
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        FileOutputStream outputStream = null;
        PdfDocument pdfDocument = null;

        try {
            outputStream = new FileOutputStream(destination.getFileDescriptor());

            if (printAttributes == null) {
                callback.onWriteFailed("PrintAttributes is null");
                return;
            }

            pdfDocument = new PrintedPdfDocument(context, printAttributes);

            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                return;
            }

            // Calculate the available page area
            int availableWidth = contentLayout.getWidth();
            int availableHeight = contentLayout.getHeight();
            int contentWidth = availableWidth;
            int contentHeight = availableHeight;

            // Calculate the number of pages required
            int pageCount = 1; // Start with one page
            int contentRemainingHeight = availableHeight;

            // Loop through the pages and write content to each page
            for (int i = 0; i < pageCount; i++) {
                // Start the page
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(availableWidth, availableHeight, i + 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // Calculate the content position for the current page
                int contentTop = 0;
                int contentBottom = contentTop + availableHeight;
                if (contentBottom > contentLayout.getHeight()) {
                    contentBottom = contentLayout.getHeight();
                }

                // Draw the content layout on the page canvas
                canvas.translate(0, contentTop);
                contentLayout.draw(canvas);

                // Finish the page
                pdfDocument.finishPage(page);

                // Calculate the remaining content height for the next page
                contentRemainingHeight = contentLayout.getHeight() - contentBottom;
            }

            // Write the PDF document to the output stream
            pdfDocument.writeTo(outputStream);

            // Return the written pages
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (IOException e) {
            e.printStackTrace();
            callback.onWriteFailed(e.getMessage());
        } finally {
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
