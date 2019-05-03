On Error Resume Next
dim fso, thisFolder

main()

sub main()

On Error Resume Next
Set fso = CreateObject("Scripting.FileSystemObject")

Set thisFolder = fso.GetFile(WScript.ScriptFullName)

del_temp(thisFolder.ParentFolder + "\.gradle")
del_temp(thisFolder.ParentFolder + "\build")
del_temp(thisFolder.ParentFolder + "\app\build")

Set fso = Nothing
Set thisFolder = Nothing
End sub

Function del_temp(FlNae)

On Error Resume Next
Dim ObjFol

Set ObjFol = fso.GetFolder(FlNae)
fso.DeleteFolder ObjFol.Path

Set ObjFol = Nothing
End Function