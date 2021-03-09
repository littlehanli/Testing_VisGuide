
////// main ////// 
$(document).ready(function(){   
    console.log("New Page");
    //var chart_datas = get_chart_datas()
    //console.log(chart_datas)
    $.ajax({
        type: 'POST',
        url:"http://127.0.0.1:5000/get_seq_data",
        //data:JSON.stringify(seq_data)
    }).done(function(responce){
        console.log(responce)
    })
})

