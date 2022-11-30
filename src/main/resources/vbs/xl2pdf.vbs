Option Explicit
Dim objExcel, strExcelPath, objSheet

strExcelPath = "XL_FILE"


Set objExcel = CreateObject("Excel.Application")
objExcel.WorkBooks.Open strExcelPath
Set objSheet = objExcel.ActiveWorkbook.Worksheets(1)

objSheet.ExportAsFixedFormat 0, "PDF_FILE",0, 1, 0, , , 0

objExcel.ActiveWorkbook.Close
objExcel.Application.Quit