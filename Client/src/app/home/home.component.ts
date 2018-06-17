import {Component, HostListener, OnInit} from '@angular/core';
import {GoogleCharts} from 'google-charts';
import {ProjectService} from "../project/project.service";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private projectService: ProjectService, private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    let homeComp = this;
    GoogleCharts.load(function () {
      homeComp.drawCharts(homeComp.projectService, homeComp.snackBar);
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.drawCharts(this.projectService, this.snackBar);
  }

  drawCharts(projectService: ProjectService, snackBar: MatSnackBar) {
    drawWeeklyTimeSumUp(projectService, snackBar);
    drawWeeklyProjectSumUp(projectService, snackBar);
  }

}

export function drawWeeklyTimeSumUp(projectService: ProjectService, snackBar: MatSnackBar) {
  projectService.fetchWeeklySumUpForTracks().subscribe(response => {
      let dataArray = [];
      dataArray.push(['Chart thing', 'Chart amount']);
      for (let timeTrack in response) {
        dataArray.push([timeTrack, response[timeTrack]]);
      }
      const data = GoogleCharts.api.visualization.arrayToDataTable(dataArray);
      const options = {
        title: 'Weekly time sum up',
        legend: {position: 'none'}
      };

      const weeklyTimeSumUpPieChart = new GoogleCharts.api.visualization.PieChart(document.getElementById('weeklyTimeSumUpPieChart'));
      weeklyTimeSumUpPieChart.draw(data, options);
    }, error => {
      snackBar.open('Error receiving data for weekly time sum up', null, {duration: 1000});
    }
  );
}

export function drawWeeklyProjectSumUp(projectService: ProjectService, snackBar: MatSnackBar) {
  projectService.fetchWeeklySumUpForProjects().subscribe(response => {
      let dataArray = [];
      let i = 0;
      let titleArray = ['Day of week'];
      for (let day in response) {
        let dayArray = [new Date(day)];
        for (let project in response[day]) {
          if (i === 0) {
            titleArray.push(project);
          }
          dayArray.push(response[day][project]);
        }
        if (i === 0) {
          dataArray.push(titleArray);
          i++;
        }
        dataArray.push(dayArray);
      }
      const data = GoogleCharts.api.visualization.arrayToDataTable(dataArray);
      const options = {
        title: 'Weekly project sum up',
        tooltip: {
          isHtml: false,
          showTitle: false
        },
        hAxis: {
          format: 'yyyy/MM/dd',
          title: 'Days'
        },
        vAxis: {
          format: "##",
          title: 'Hours'
        },
        legend: {position: 'none'}
      };

      const weeklyProjectSumUpColumnChart = new GoogleCharts.api.visualization.ColumnChart(document.getElementById('weeklyProjectSumUpColumnChart'));
      weeklyProjectSumUpColumnChart.draw(data, options);
    }, error => {
      snackBar.open('Error receiving data for weekly project sum up', null, {duration: 1000});
    }
  );
}
