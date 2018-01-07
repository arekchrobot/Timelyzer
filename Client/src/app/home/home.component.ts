import {Component, HostListener, OnInit} from '@angular/core';
import {GoogleCharts} from 'google-charts';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor() {
  }

  ngOnInit() {
    GoogleCharts.load(this.drawCharts);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.drawCharts();
  }

  drawCharts() {
    drawWeeklyTimeSumUp();
    drawWeeklyProjectSumUp();
  }

}

export function drawWeeklyTimeSumUp() {
  const data = GoogleCharts.api.visualization.arrayToDataTable([
    ['Chart thing', 'Chart amount'],
    ['Development', 60],
    ['Research', 15],
    ['Bugfix', 25],
    ['Days off', 0],
    ['Intrudoction', 0]
  ]);

  const options = {
    title: 'Weekly time sum up',
    legend: {position: 'none'}
  };

  const weeklyTimeSumUpPieChart = new GoogleCharts.api.visualization.PieChart(document.getElementById('weeklyTimeSumUpPieChart'));
  weeklyTimeSumUpPieChart.draw(data, options);
}

export function drawWeeklyProjectSumUp() {
  const data = GoogleCharts.api.visualization.arrayToDataTable([
    ['Day of week', 'Project 1', 'Project 2', 'Project 3'],
    [new Date(new Date().setDate(new Date().getDate()-7)), 4,2,2],
    [new Date(new Date().setDate(new Date().getDate()-6)), 1,5,2],
    [new Date(new Date().setDate(new Date().getDate()-5)),0,8,0],
    [new Date(new Date().setDate(new Date().getDate()-4)), 1,1,6],
    [new Date(new Date().setDate(new Date().getDate()-3)), 3,2,3],
    [new Date(new Date().setDate(new Date().getDate()-2)),0,0,0],
    [new Date(new Date().setDate(new Date().getDate()-1)), 0,0,0]
  ]);

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
}
