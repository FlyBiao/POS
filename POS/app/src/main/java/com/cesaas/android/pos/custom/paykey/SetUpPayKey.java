package com.cesaas.android.pos.custom.paykey;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesaas.android.pos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author FGB
 * Description
 * Created at 2017/10/26 10:51
 * Version 1.0
 */

public class SetUpPayKey {
    private static int numIndex=0;
    private static List<String> numList = new ArrayList<String>();

    public static void setUpPayKeyAdapter(GridView gridview, final EditText editMemberAmount, final Context ct){
        final PayKeyAdapter adapter = new PayKeyAdapter(ct);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String keyType = view.findViewById(R.id.GridTextView).getTag().toString();
                String textValue = ((TextView)view.findViewById(R.id.GridTextView)).getText().toString();
                String editText = editMemberAmount.getText().toString();
                if(keyType.equals("num")){
                    //数字键
                    try{
                        String orgValue = numList.get(numIndex);
                        if(editText.equals("0")){
                            editMemberAmount.setText(textValue);
                        }else{
                            //小数点最多只能后两位
                            if(orgValue.equals("0.00")){
                                orgValue=textValue;
                                numList.set(numIndex, textValue);
                                editMemberAmount.setText(textValue);
                                return;
                            }
                            if(orgValue.lastIndexOf(".")!=-1 && orgValue.lastIndexOf(".")<orgValue.length()-2){
                                Toast.makeText(ct,"只能输入小数点后两位",Toast.LENGTH_SHORT).show();
                            }else{
                                if(editText.length()>=18){
                                    Toast.makeText(ct,"一次输入算式无法超过18个字符",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int numLen = orgValue.length();
                                if(orgValue.lastIndexOf(".")!=-1){
                                    numLen = orgValue.lastIndexOf(".")+1;
                                }
                                if(numLen==7){
                                    Toast.makeText(ct,"收款金额不能超过7位数",Toast.LENGTH_SHORT).show();
                                }else{
                                    numList.set(numIndex, orgValue+textValue);
                                    editMemberAmount.setText(editText+textValue);
                                }
                            }
                        }
                    }catch(Exception e){
                        numIndex=0;
                        numList=new ArrayList<String>();
                        numList.add(textValue);
                        editMemberAmount.setText(textValue);
                    }
                }
                if(keyType.equals("back")){
                    //退格
                    try{
                        if(editText.lastIndexOf("+") == editText.length()-1){
                            //最后为加则减少一位
                            numList.set(numIndex,"");
                            numIndex--;
                            editMemberAmount.setText(editText.substring(0,editText.length()-1));
                        }else{
                            String orgValue = numList.get(numIndex);
                            if(!orgValue.equals("")){
                                numList.set(numIndex, orgValue.substring(0,orgValue.length()-1));
                            }
                            if(editText.equals("0")||editText.length()==1){
                                editMemberAmount.setText("0.00");
                                numList.set(numIndex,"0.00");
                            }else{
                                editMemberAmount.setText(editText.substring(0,editText.length()-1));
                            }
                        }
                    }catch(Exception e){
                        numIndex=0;
                        numList=new ArrayList<String>();
                        numList.add("0");
                        editMemberAmount.setText("");
                    }
                }
                if(keyType.equals("point")){
                    //小数点
                    try{
                        String orgValue = numList.get(numIndex);
                        if(orgValue.lastIndexOf(".")!=-1){
                            Toast.makeText(ct,"已存在小数点",Toast.LENGTH_SHORT).show();
                        }else{
                            if(editText.lastIndexOf("+") == editText.length()-1){
                                //最后为加则补0
                                numList.set(numIndex, orgValue+"0"+textValue);
                                editMemberAmount.setText(editText+"0"+textValue);
                            }else{
                                numList.set(numIndex, orgValue+textValue);
                                editMemberAmount.setText(editText+textValue);
                            }
                        }
                    }catch(Exception e){
                        numIndex=0;
                        numList=new ArrayList<String>();
                        numList.add("0.");
                        editMemberAmount.setText("0.");
                    }
                }

            }
        });
    }

    /**
     * 清空
     */
    private void clearInputData(EditText editMemberAmount,String EditTxtValue){
        numIndex=0;
        numList=new ArrayList<String>();
        editMemberAmount.setText("");
        EditTxtValue="0";

    }
}
