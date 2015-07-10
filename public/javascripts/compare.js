$(document).ready( function(){ 
	var data = {
    labels: ["January", "February", "March", "April", "May", "June", "July"],
    datasets: [
        {
            label: "My First dataset",
            fillColor: "rgba(220,220,220,0.5)",
            strokeColor: "rgba(220,220,220,0.8)",
            highlightFill: "rgba(220,220,220,0.75)",
            highlightStroke: "rgba(220,220,220,1)",
            data: [65, 59, 80, 81, 56, 55, 40]
        },
        {
            label: "My Second dataset",
            fillColor: "rgba(151,187,205,0.5)",
            strokeColor: "rgba(151,187,205,0.8)",
            highlightFill: "rgba(151,187,205,0.75)",
            highlightStroke: "rgba(151,187,205,1)",
            data: [28, 48, 40, 19, 86, 27, 90]
        }
		]
	};
	var ctx = document.getElementById("compareChart").getContext("2d");
	var barChart = new Chart(ctx).Bar(data, {
		responsive:true		
	});
	
	var data2 =[ {
        value: 300,
        color:"#F7464A",
        highlight: "#FF5A5E",
        label: "Interpersonal"
    },
    {
        value: 50,
        color: "#46BFBD",
        highlight: "#5AD3D1",
        label: "Technical"
    },
    {
        value: 100,
        color: "#FDB45C",
        highlight: "#FFC870",
        label: "Management"
    }];
	var ctx2 = document.getElementById("compareChartSkills").getContext("2d");
	var pieChart = new Chart(ctx2).Doughnut(data2, {
		responsive:true		
	});
	$( '#compareChartSkillsLegend' ).html( pieChart.generateLegend() );
});
